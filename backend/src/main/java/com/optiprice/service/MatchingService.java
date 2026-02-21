package com.optiprice.service;

import com.optiprice.dto.response.CategoryResponse;
import com.optiprice.dto.response.MatchResponse;
import com.optiprice.model.StoreItem;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final ChatClient chatClient;
    private final MasterProductService masterProductService;
    private final Semaphore aiPermits = new Semaphore(3);
    private final ConcurrentHashMap<String, ReentrantLock> itemLocks = new ConcurrentHashMap<>();

    public void findOrCreateMasterProduct(StoreItem item) {
        findOrCreateMasterProduct(item, null);
    }


    public void findOrCreateMasterProduct(StoreItem item, String knownCategory) {

        String lockKey = item.getStoreSpecificName().trim().toLowerCase();
        ReentrantLock lock = itemLocks.computeIfAbsent(lockKey, k -> new ReentrantLock());

        lock.lock();
        try {
            aiPermits.acquire();
            try {
                String category;

                if (knownCategory != null && !knownCategory.trim().isEmpty()) {
                    category = knownCategory;
                } else {
                    category = predictCategory(item.getStoreSpecificName());
                }

                String itemLabel = item.getBrand() + " " + item.getStoreSpecificName();

                List<Document> similarProducts = masterProductService.findSimilarProducts(itemLabel);

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

                MatchResponse response = chatClient.prompt()
                        .user(u -> u.text("""
                SYSTEM: You are a high-precision Data Auditor for a grocery price aggregator.
                
                TASK: Compare the "NEW ITEM" to the list of "CANDIDATES" and determine if an EXACT match exists.
                
                NEW ITEM: "{name}" (Brand: {brand})
                CANDIDATES:
                {candidates}
                
                STRICT MATCHING RULES:
                1. DIFFERENT SIZES/VOLUMES = NO MATCH. (e.g., 50g is NOT 125g).
                2. MULTI-PACKS = NO MATCH. (e.g., "6 x 1L" is NOT "1L").
                3. DIFFERENT BRANDS = NO MATCH. (e.g., Clover is NOT PnP).
                
                OUTPUT INSTRUCTIONS:
                You must fill out the JSON fields in this exact logical order:
                1. "extracted_new_item_size": Write the size of the NEW ITEM (e.g., "50g", "1L"). If none, write "none".
                2. "extracted_candidate_size": Write the size of the best matching CANDIDATE.
                3. "reasoning": State if the sizes and brands match exactly.
                4. "match": true ONLY IF sizes and brands are identical. Otherwise false.
                5. "candidate_id": the ID of the matched candidate, or null.
                """)
                                .param("name", item.getStoreSpecificName())
                                .param("brand", item.getBrand())
                                .param("candidates", candidateList))
                        .call()
                        .entity(MatchResponse.class);

                if (response != null) {
                    System.out.println("AI Thought Process: " + item.getStoreSpecificName());
                    System.out.println(" - New Size: " + response.extracted_new_item_size());
                    System.out.println(" - Cand Size: " + response.extracted_candidate_size());
                    System.out.println(" - MATCH: " + response.match());
                }

                if (response != null && response.match() && response.candidate_id() != null) {
                    masterProductService.linkToExistingMaster(item, response.candidate_id(), category);
                } else {
                    masterProductService.createNewMasterProduct(item, category);
                }

            } finally {
                aiPermits.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("AI Task Interrupted: " + item.getId());
        } catch (Exception e) {
            System.err.println("AI Matching failed: " + e.getMessage());
            masterProductService.createNewMasterProduct(item, "General"); // Fallback
        } finally {
            lock.unlock();
            if (!lock.hasQueuedThreads()) {
                itemLocks.remove(lockKey);
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
