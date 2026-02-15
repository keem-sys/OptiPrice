package com.optiprice.service;

import com.optiprice.model.PriceLog;
import com.optiprice.model.Store;
import com.optiprice.model.StoreItem;
import com.optiprice.repository.PriceLogRepository;
import com.optiprice.repository.StoreItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class StoreItemService {

    private final StoreItemRepository itemRepo;
    private final PriceLogRepository priceLogRepo;
    private final MatchingService matchingService;

    @Transactional
    public void saveOrUpdateItem(Store store, String externalId, String name, String brand,
                                 BigDecimal price, String imageUrl, String productUrl) {

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

        if (imageUrl != null) item.setImageUrl(imageUrl);
        if (productUrl != null) item.setProductUrl(productUrl);

        StoreItem savedItem = itemRepo.save(item);

        if (savedItem.getMasterProduct() == null) {
            matchingService.findOrCreateMasterProduct(savedItem);
        }

        PriceLog log = PriceLog.builder()
                .price(price)
                .timestamp(now)
                .storeItem(savedItem)
                .build();

        priceLogRepo.save(log);
    }
}