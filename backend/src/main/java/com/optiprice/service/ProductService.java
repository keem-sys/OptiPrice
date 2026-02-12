package com.optiprice.service;

import com.optiprice.dto.response.MasterProductResponse;
import com.optiprice.dto.response.ProductSearchResponse;
import com.optiprice.dto.response.StoreItemResponse;
import com.optiprice.model.MasterProduct;
import com.optiprice.model.PriceLog;
import com.optiprice.model.Store;
import com.optiprice.model.StoreItem;
import com.optiprice.repository.MasterProductRepository;
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
    private final MasterProductRepository masterProductRepository;

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

    public List<MasterProductResponse> searchProducts(String query) {
        List<MasterProduct> masters = masterProductRepository.searchByKeyword(query);

        // 2. Map to DTOs
        return masters.stream()
                .map(master -> new MasterProductResponse(
                        master.getId(),
                        master.getGenericName(),
                        master.getCategory(),
                        master.getStoreItems().stream()
                                .map(item -> new StoreItemResponse(
                                        item.getId(),
                                        item.getStore().getName(),
                                        item.getStore().getLogoUrl(),
                                        item.getStoreSpecificName(),
                                        item.getCurrentPrice(),
                                        item.getProductUrl(),
                                        item.getImageUrl(),
                                        item.getLastUpdated()
                                ))
                                .toList()
                ))
                .toList();
    }
}