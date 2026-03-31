package com.optiprice.service;

import com.optiprice.model.MasterProduct;
import com.optiprice.model.PriceLog;
import com.optiprice.model.Store;
import com.optiprice.model.StoreItem;
import com.optiprice.repository.MasterProductRepository;
import com.optiprice.repository.PriceLogRepository;
import com.optiprice.repository.StoreItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class StoreItemService {

    private final StoreItemRepository itemRepo;
    private final PriceLogRepository priceLogRepo;
    private final MasterProductRepository masterProductRepository;

    @Transactional
    @CacheEvict(value = "history", key = "#result.masterProduct.id", condition = "#result.masterProduct != null")
    public StoreItem saveOrUpdateItem(Store store, String externalId, String name, String brand,
                                 BigDecimal price, BigDecimal oldPrice, String imageUrl, String productUrl, String knownCategory,
                                      String barcode, Boolean isOnPromotion, String promotionText, String articleSku) {

        OffsetDateTime now = OffsetDateTime.now();

        StoreItem item = itemRepo.findByExternalIdAndStore(externalId, store)
                .orElse(new StoreItem());

        if (item.getId() == null) {
            item.setStore(store);
            item.setExternalId(externalId);
        }

        item.setStoreSpecificName(name);
        item.setBrand(brand);
        item.setCurrentPrice(price);
        item.setLastUpdated(now);
        item.setBarcode(barcode);

        item.setOldPrice(oldPrice);
        item.setIsOnPromotion(isOnPromotion != null ? isOnPromotion : false);
        item.setPromotionText(promotionText);
        item.setArticleSku(articleSku);

        if (imageUrl != null) item.setImageUrl(imageUrl);
        if (productUrl != null) item.setProductUrl(productUrl);


        if (item.getMasterProduct() == null && barcode != null && !barcode.isEmpty()) {
            itemRepo.findFirstByBarcode(barcode).ifPresent(existingItem -> {
                if (existingItem.getMasterProduct() != null) {
                    item.setMasterProduct(existingItem.getMasterProduct());
                    System.out.println("BARCODE MATCH: Linked '" + name + "' to existing Master Product.");
                }
            });
        }

        if (item.getMasterProduct() == null && articleSku != null && !articleSku.isEmpty()) {
            itemRepo.findFirstByArticleSkuAndMasterProductIsNotNull(articleSku).ifPresent(existingSibling -> {
                MasterProduct master = existingSibling.getMasterProduct();
                item.setMasterProduct(master);
                System.out.println("SKU MATCH: " + name);

                if (item.getBarcode() == null || item.getBarcode().isEmpty()) {
                    master.getStoreItems().stream()
                            .filter(si -> si.getBarcode() != null && !si.getBarcode().isEmpty())
                            .findFirst()
                            .ifPresent(barcodeSibling -> {
                                item.setBarcode(barcodeSibling.getBarcode());
                                System.out.println("DATA ENRICHMENT: Found Barcode from " + barcodeSibling.getStore().getName());
                            });
                }
            });
        }

        String cleanName = name.replaceAll("(?i)\\b(Fresh|Instant|Eco|Premium)\\b", "").trim();
        cleanName = cleanName.replaceAll("\\s+", " ");

        if (item.getMasterProduct() == null) {
            String finalCleanName = cleanName;
            masterProductRepository.findByGenericNameIgnoreCase(cleanName)
                    .stream()
                    .findFirst()
                    .ifPresent(existingMaster -> {
                        item.setMasterProduct(existingMaster);
                        System.out.println("EXACT NAME MATCH: Linked '" + finalCleanName + "' to existing Master.");

                        if (item.getBarcode() == null || item.getBarcode().isEmpty()) {

                            existingMaster.getStoreItems().stream()
                                    .filter(sibling -> sibling.getBarcode() != null && !sibling.getBarcode().isEmpty())
                                    .findFirst()
                                    .ifPresent(siblingWithBarcode -> {
                                        item.setBarcode(siblingWithBarcode.getBarcode());
                                        System.out.println("BACKFILLED BARCODE: '" +
                                                siblingWithBarcode.getBarcode() + "' to " + name);
                                    });
                        }
                    });
        }

        if (item.getMasterProduct() == null) {
            MasterProduct newMaster = new MasterProduct();

            newMaster.setGenericName(cleanName);
            newMaster.setCategory(knownCategory != null ? knownCategory : "General");

            MasterProduct savedMaster = masterProductRepository.save(newMaster);
            item.setMasterProduct(savedMaster);

            System.out.println("NEW PRODUCT: Created Master Product for '" + cleanName + "'");
        }

        StoreItem savedItem = itemRepo.saveAndFlush(item);

//        // 3. IF LINKED, UPDATE REGISTRY
//        if (savedItem.getMasterProduct() != null) {
//            barcodeRegistry.put(barcode, savedItem.getMasterProduct().getId());
//        }
//
//
//        if (savedItem.getMasterProduct() == null) {
//            System.out.println("No barcode match. Sending to AI: " + name);
//            if (knownCategory != null) {
//                matchingService.findOrCreateMasterProduct(savedItem, knownCategory);
//            } else {
//                eventPublisher.publishEvent(new ProductScrapedEvent(savedItem.getId()));
//            }
//        }

        PriceLog log = PriceLog.builder()
                .price(price)
                .timestamp(now)
                .storeItem(savedItem)
                .build();

        priceLogRepo.save(log);
        return savedItem;
    }
}