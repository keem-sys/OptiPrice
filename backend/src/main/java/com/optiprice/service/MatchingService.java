package com.optiprice.service;

import com.optiprice.dto.response.CategoryResponse;
import com.optiprice.dto.response.MatchResponse;
import com.optiprice.model.StoreItem;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
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

        String rawName = item.getStoreSpecificName();
        String cleanName = normalizeString(rawName);

        String brandStr = (item.getBrand() != null && !item.getBrand().isEmpty())
                ? item.getBrand() : "unknown_brand";
        String lockKey = brandStr.trim().toLowerCase();
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

                String itemLabel = item.getBrand() + " " + cleanName;

                List<Document> similarProducts = masterProductService.findSimilarProducts(itemLabel);

                if (similarProducts.isEmpty()) {
                    masterProductService.createNewMasterProduct(item, category);
                    return;
                }

                String cleanNewItem = normalizeString(itemLabel);

                for (Document doc : similarProducts) {
                    String cleanCandidate = normalizeString(doc.getText());

                    if (cleanNewItem.equals(cleanCandidate)) {
                        System.out.println("JAVA EXACT MATCH (Skipping AI): " + item.getStoreSpecificName());
                        String bypassId = doc.getMetadata().get("master_id").toString();
                        masterProductService.linkToExistingMaster(item, bypassId, category);
                        return;
                    }
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
                
                TASK: Compare the "NEW ITEM" to the list of "CANDIDATES" and determine if they are the exact 
                same real-world product.
                
                NEW ITEM: "{name}" (Brand: {brand})
                CANDIDATES:
                {candidates}
                
                STRICT MATCHING RULES:
                1. PHYSICAL OBJECT MUST MATCH: "Chocolate Slab" is NOT "Liquid Milk". "Coffee" is NOT "Tea".
                2. BRAND IS THE MOST IMPORTANT: "Clover" is NOT "Fair Cape". "PnP" is NOT "Spar".
                3. DIFFERENT SIZES/VOLUMES = NO MATCH. (e.g., 50g is NOT 125g).
                4. FAT CONTENT IS CRITICAL: "Full Cream" is NOT "Low Fat". "2%" is NOT "Full Cream". "Fat Free" is NOT "Low Fat".
                5. MULTIPACKS ARE DIFFERENT: "6 x 1L" is NOT "1L". "Pack of 6" is NOT "Single".
                6. BRAND VARIATIONS ARE OKAY: "Fair Cape" matches "Fair Cape Dairies". "Coca-Cola" matches "Coke".
                7. SYNONYMS MATCH: "UHT" is exactly the same as "Long Life".
                8. IGNORE MARKETING FLUFF: Words like "Eco", "Premium", or "Fresh" do not make a product different.
                9. FLAVOR/TYPE MUST MATCH: "Whole Nut" is NOT "Plain Milk Chocolate". "Fruit & Nut" is NOT "Whole Nut". "Salted" is NOT "Unsalted". "Cheddar" is NOT "Gouda".
                10. CASE-INSENSITIVE UNITS: "1l" is exactly the same as "1L".
                
                OUTPUT INSTRUCTIONS:
                You must fill out the JSON fields in this exact logical order:
                1. "new_item_physical_object": What is this item? (e.g., "Liquid Milk", "Chocolate Slab", "Cheese").
                2. "candidate_physical_object": What is the candidate? (e.g., "Liquid Milk", "Chocolate Slab").
                3. "extracted_new_brand": Extract the brand of the NEW ITEM.
                4. "extracted_candidate_brand": Extract the brand of the best matching CANDIDATE.
                5. "extracted_new_item_size": Write the size of the NEW ITEM (standardize to uppercase, e.g., "1L").
                6. "extracted_candidate_size": Write the size of the best matching CANDIDATE.
                7. "core_product_type": Strip away fluff, but YOU MUST KEEP the fat content or flavor (e.g., if it says "Whole Nut", write "Whole Nut Chocolate". If "Milk Chocolate", write "Milk Chocolate").
                8. "reasoning": State if the sizes, brands, and core product match based on the rules.
                9. "candidate_id": CRITICAL! Extract the exact numeric ID from the CANDIDATES list for the matching item (e.g., if candidate is "ID 55: Milk", return "55"). If no match, return null.
                10. "match": true ONLY IF sizes and core products are identical. Otherwise false.
                """)
                                .param("name", item.getStoreSpecificName())
                                .param("brand", item.getBrand())
                                .param("candidates", candidateList))
                        .call()
                        .entity(MatchResponse.class);

                if (response != null) {
                    System.out.println("AI Thought Process: " + item.getStoreSpecificName());
                    System.out.println(" - Brands: " + response.extracted_new_brand() + " vs " + response.extracted_candidate_brand());
                    System.out.println(" - Sizes: " + response.extracted_new_item_size() + " vs " + response.extracted_candidate_size());
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

    private String normalizeString(String input) {
        if (input == null) return "";

        String s = Normalizer.normalize(input, Normalizer.Form.NFD);
        s = s.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        s = s.replaceAll("(?i)(\\d+)\\s*l\\b", "$1L");
        s = s.replaceAll("(?i)(\\d+)\\s*ml\\b", "$1ML");
        s = s.replaceAll("(?i)(\\d+)\\s*g\\b", "$1G");
        s = s.replaceAll("(?i)(\\d+)\\s*kg\\b", "$1KG");

        s = s.replaceAll("(?i)\\b(Fresh|Instant|Eco|Premium)\\b", "");

        return s.trim().replaceAll("\\s+", " ").toLowerCase();
    }


}
