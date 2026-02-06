package com.optiprice.service;

import com.optiprice.dto.response.MatchResponse;
import com.optiprice.model.MasterProduct;
import com.optiprice.model.StoreItem;
import com.optiprice.repository.MasterProductRepository;
import com.optiprice.repository.StoreItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final VectorStore vectorStore;
    private final ChatClient.Builder chatClientBuilder;
    private final MasterProductRepository masterProductRepository;
    private final StoreItemRepository itemRepository;

    @Transactional
    public void findOrCreateMasterProduct(StoreItem item) {
        String itemLabel = item.getBrand() + " " + item.getStoreSpecificName();

        List<Document> similarProducts = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(itemLabel)
                        .topK(3)
                        .similarityThreshold(0.85)
                        .build()
        );

        if (similarProducts.isEmpty()) {
            createNewMasterProduct(item);
            return;
        }

        String candidateList = similarProducts.stream()
                .map(doc -> {
                    String masterId = doc.getMetadata().get("master_id").toString();
                    return " - ID " + masterId + ": " + doc.getText();
                })
                .collect(Collectors.joining("\n"));

        try {
            MatchResponse response = chatClientBuilder.build()
                    .prompt()
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
                linkToExistingMaster(item, response.candidate_id());
            } else {
                createNewMasterProduct(item);
            }
        } catch (Exception e) {
            System.err.println("AI Matching failed: " + e.getMessage());
            // IF ALL HELL BREAK LOOSE DUPLICATE
            createNewMasterProduct(item);
        }
    }

    private void createNewMasterProduct(StoreItem storeItem) {
        System.out.println("AI: Creating NEW Master Product for: " + storeItem.getStoreSpecificName());

        MasterProduct masterProduct = new MasterProduct();
        masterProduct.setGenericName(storeItem.getStoreSpecificName());
        masterProduct.setCategory("General");
        MasterProduct savedMaster = masterProductRepository.save(masterProduct);

        storeItem.setMasterProduct(savedMaster);
        itemRepository.save(storeItem);

        Document vectorDoc = new Document(
                storeItem.getBrand() + " " + storeItem.getStoreSpecificName(),
                Map.of("master_id", savedMaster.getId())
        );
        vectorStore.add(List.of(vectorDoc));
    }

    private void linkToExistingMaster(StoreItem item, String masterIdString) {
        try {
            String cleanId = masterIdString.replaceAll("[^0-9]", "");
            Long masterId = Long.parseLong(cleanId);
            System.out.println("AI: MATCH FOUND! Linking '" + item.getStoreSpecificName() + "' to Master ID: " + masterId);

            masterProductRepository.findById(masterId).ifPresent(master -> {
                item.setMasterProduct(master);
                itemRepository.save(item);
            });
        } catch (Exception e) {
            System.err.println("Failed to link matched ID: " + masterIdString);
            createNewMasterProduct(item);
        }
    }
}
