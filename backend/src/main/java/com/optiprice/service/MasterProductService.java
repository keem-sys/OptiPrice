package com.optiprice.service;

import com.optiprice.model.MasterProduct;
import com.optiprice.model.StoreItem;
import com.optiprice.repository.MasterProductRepository;
import com.optiprice.repository.StoreItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MasterProductService {

    private final MasterProductRepository masterProductRepository;
    private final StoreItemRepository itemRepository;
    private final VectorStore vectorStore;

    @Transactional
    public void createNewMasterProduct(StoreItem storeItem, String predictedCategory) {
        String brand = storeItem.getBrand();
        String specificName = storeItem.getStoreSpecificName();

        String fullName = (specificName.toLowerCase().startsWith(brand.toLowerCase()))
                ? specificName
                : brand + " " + specificName;

        System.out.println("DB: Creating Master Product: " + fullName + " [" + predictedCategory + "]");

        MasterProduct masterProduct = new MasterProduct();
        masterProduct.setGenericName(fullName);
        masterProduct.setCategory(predictedCategory);
        MasterProduct savedMaster = masterProductRepository.save(masterProduct);

        storeItem.setMasterProduct(savedMaster);
        itemRepository.save(storeItem);

        Document vectorDoc = new Document(
                fullName,
                Map.of("master_id", savedMaster.getId())
        );
        vectorStore.add(List.of(vectorDoc));
    }

    @Transactional
    public void createMasterProductWithKnownCategory(StoreItem storeItem, String knownCategory) {
        System.out.println("DB: Creating Master Product with KNOWN Category: [" + knownCategory + "]");
        createNewMasterProduct(storeItem, knownCategory);
    }

    @Transactional
    public void linkToExistingMaster(StoreItem item, String masterIdString, String fallbackCategory) {
        try {
            String cleanId = masterIdString.replaceAll("[^0-9]", "");
            Long masterId = Long.parseLong(cleanId);
            System.out.println("DB: Linking '" + item.getStoreSpecificName() + "' to Master ID: " + masterId);

            masterProductRepository.findById(masterId).ifPresentOrElse(
                    master -> {
                        item.setMasterProduct(master);
                        itemRepository.save(item);
                    },
                    () -> {
                        createNewMasterProduct(item, fallbackCategory);
                    }
            );
        } catch (Exception e) {
            System.err.println("Failed to link matched ID, falling back to new.");
            createNewMasterProduct(item, fallbackCategory);
        }
    }

    @Transactional(readOnly = true)
    public List<Document> findSimilarProducts(String itemLabel) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(itemLabel)
                        .topK(3)
                        .similarityThreshold(0.85)
                        .build()
        );
    }
}