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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final MasterProductService masterProductService;

    public void findOrCreateMasterProduct(StoreItem item) {
        String category = predictCategory(item.getStoreSpecificName());
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
                        You are a strict data auditor. Compare this Grocery Item to the Candidates.
                        NEW ITEM: "{name}" (Brand: {brand})
    
                        CANDIDATES:
                        {candidates}
    
                        STRICT MATCHING RULES:
                        1. BRAND MUST MATCH: "Clover" is not a match for "Ritebrand" or "PnP".
                        2. SIZE MUST MATCH: "1L" is not a match for "250ml" or "6 x 1L".
                        3. PRODUCT TYPE MUST MATCH: "Milk Bottles" (Sweets) is not a match for "Milk Bottle" (Glass Container).
    
                        If it is not a 100% identical product, set match=false.
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
            // IF ALL HELL BREAK LOOSE DUPLICATE
            masterProductService.createNewMasterProduct(item, category);
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
