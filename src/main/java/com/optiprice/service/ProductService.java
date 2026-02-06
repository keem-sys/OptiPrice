package com.optiprice.service;

import com.optiprice.dto.response.ProductSearchResponse;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final StoreItemRepository itemRepository;
    private final PriceLogRepository priceLogRepository;
    private final MatchingService matchingService;

    @Transactional
    public void processScrapedProduct(Store store, String externalId, String name,
                                      String brand, BigDecimal price,
                                      String imageUrl, String productUrl) {

        OffsetDateTime now = OffsetDateTime.now();
        StoreItem item = itemRepository.findByExternalIdAndStore(externalId, store)
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

        StoreItem savedItem = itemRepository.save(item);

        if (savedItem.getMasterProduct() == null) {
            matchingService.findOrCreateMasterProduct(savedItem);
        }

        PriceLog log = PriceLog.builder()
                .price(price)
                .timestamp(now)
                .storeItem(savedItem)
                .build();

        priceLogRepository.save(log);
    }

    public List<ProductSearchResponse> searchProducts(String query) {

        List<StoreItem> items = itemRepository.findByStoreSpecificNameContainingIgnoreCase(query);

        return items.stream()
                .map(item -> new ProductSearchResponse(
                        item.getStore().getName(),
                        item.getStore().getLogoUrl(),
                        (item.getMasterProduct() != null)
                                ? item.getMasterProduct().getGenericName()
                                : item.getStoreSpecificName(),
                        item.getCurrentPrice(),
                        item.getBrand(),
                        item.getImageUrl(),
                        item.getProductUrl(),
                        item.getLastUpdated()
                ))
                .toList();
    }
}