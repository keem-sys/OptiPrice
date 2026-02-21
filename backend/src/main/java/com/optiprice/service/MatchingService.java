package com.optiprice.service;

import com.optiprice.dto.response.CategoryResponse;
import com.optiprice.dto.response.MatchResponse;
import com.optiprice.model.StoreItem;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final MasterProductService masterProductService;
    private final Semaphore aiPermits = new Semaphore(5);

    public void findOrCreateMasterProduct(StoreItem item) {
        findOrCreateMasterProduct(item, null);
    }


    public void findOrCreateMasterProduct(StoreItem item, String knownCategory) {

        String lockKey = item.getStoreSpecificName().intern();
        synchronized (lockKey) {
            try {
                aiPermits.acquire();
                String category;

                if (knownCategory != null && !knownCategory.trim().isEmpty()) {
                    category = knownCategory;
                } else {
                    category = predictCategory(item.getStoreSpecificName());
                }

                String itemLabel = item.getBrand() + " " + item.getStoreSpecificName();

                List<Document> similarProducts = vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(itemLabel)
                                .topK(3)
                                .similarityThreshold(0.85)
                                .build()
                );

                if (similarProducts.isEmpty()) {
                    masterProductService.createNewMasterProduct(item, category);
                    return;
                }

                String candidateList = similarProducts.stream()
                        .map(doc -> {
                            String masterId = doc.getMetadata().get("master_id").toString();
                            return " - ID " + masterId + ": " + doc.getText();
                        })
                        .collect(Collectors.joining("\n"));

                try {
                    MatchResponse response = chatClient.prompt()
                            .user(u -> u.text("""
                    SYSTEM: You are a high-precision Data Auditor for a grocery price aggregator.
                    
                    TASK: Compare the "NEW ITEM" to the list of "CANDIDATES" and determine if an EXACT match exists.
                    
                    NEW ITEM: "{name}" (Brand: {brand})
                    CANDIDATES:
                    {candidates}
                    
                    LOGICAL STEPS FOR YOU TO FOLLOW:
                    1. Identify the BRAND of the NEW ITEM and the CANDIDATE.
                    2. Identify the QUANTITY/VOLUME (e.g., 500ml, 1L, 3L, 2kg) of both.
                    3. Compare them.
                    
                    STRICT RULES:
                    - DIFFERENT SIZES = NO MATCH. (e.g., 500ml is NOT 3L).
                    - DIFFERENT BRANDS = NO MATCH. (e.g., Clover is NOT Darling).
                    - PACK SIZE DIFFERENCE = NO MATCH. (e.g., 6 x 1L is NOT 1L).
                    - If the quantity is missing from one but present in the other, assume NO MATCH to be safe.
                    
                    If a perfect match exists, return the candidate_id.
                    If no perfect match exists, return match=false.
                    
                    Return ONLY a JSON object.
                    """)
                                    .param("name", item.getStoreSpecificName())
                                    .param("brand", item.getBrand())
                                    .param("candidates", candidateList))
                            .call()
                            .entity(MatchResponse.class);

                    if (response != null && response.match() && response.candidate_id() != null) {
                        masterProductService.linkToExistingMaster(item, response.candidate_id(), category);
                    } else {
                        masterProductService.createNewMasterProduct(item, category);
                    }
                } catch (Exception e) {
                    System.err.println("AI Matching failed: " + e.getMessage());
                    masterProductService.createNewMasterProduct(item, category);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("AI Task Interrupted: " + item.getId());
            } finally {
                aiPermits.release();
            }
        }
    }

    private String predictCategory(String productName) {
        try {
            CategoryResponse response = chatClient
                    .prompt()
                    .user(u -> u.text("""
                    Return ONLY a valid JSON object
                    Classify the grocery product: "{product}"
                    
                    Choose ONE category from this exact list:
                    - Dairy
                    - Bakery
                    - Meat & Poultry
                    - Fruit & Vegetables
                    - Pantry
                    - Beverages
                    - Frozen Food
                    - Personal Care
                    - Household Cleaning
                    - Sweets & Snacks
                    
                    Return ONLY the JSON. If unclear, return "Pantry".
                    """)
                            .param("product", productName))
                    .call()
                    .entity(CategoryResponse.class);

            return (response != null && response.category() != null) ? response.category() : "General";

        } catch (Exception e) {
            System.err.println("Category prediction failed for '" + productName + "': " + e.getMessage());
            return "General";
        }
    }


}
