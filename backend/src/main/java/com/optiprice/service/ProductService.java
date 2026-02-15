package com.optiprice.service;

import com.optiprice.dto.response.*;
import com.optiprice.model.MasterProduct;
import com.optiprice.model.PriceLog;
import com.optiprice.model.Store;
import com.optiprice.model.StoreItem;
import com.optiprice.repository.MasterProductRepository;
import com.optiprice.repository.PriceLogRepository;
import com.optiprice.repository.StoreItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final MasterProductRepository masterProductRepository;
    private final PriceLogRepository priceLogRepository;

    public PagedResponse<MasterProductResponse> searchProducts(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MasterProduct> productPage = masterProductRepository.searchByKeyword(query, pageable);

        List<MasterProductResponse> content = productPage.getContent().stream()
                .map(this::mapToMasterProductResponse)
                .toList();

        return new PagedResponse<>(
                content,
                productPage.getNumber(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }

    public List<PriceHistoryPoint> getPriceHistory(Long masterId) {
        return priceLogRepository.findHistoryByMasterId(masterId);
    }

    public MasterProductResponse getProductById(Long id) {
        return masterProductRepository.findById(id)
                .map(this::mapToMasterProductResponse)
                .orElseThrow(() -> new RuntimeException("Product with ID " + id + " not found"));
    }


    private MasterProductResponse mapToMasterProductResponse(MasterProduct master) {
        List<StoreItemResponse> itemResponses = master.getStoreItems().stream()
                .map(this::mapToStoreItemResponse)
                .toList();

        return new MasterProductResponse(
                master.getId(),
                master.getGenericName(),
                master.getCategory(),
                itemResponses
        );
    }


    private StoreItemResponse mapToStoreItemResponse(StoreItem item) {
        StoreResponse storeDto = new StoreResponse(
                item.getStore().getId(),
                item.getStore().getName(),
                item.getStore().getLogoUrl(),
                item.getStore().getWebsiteUrl()
        );

        return new StoreItemResponse(
                item.getId(),
                storeDto,
                item.getBrand(),
                item.getStoreSpecificName(),
                item.getCurrentPrice(),
                item.getProductUrl(),
                item.getImageUrl(),
                item.getLastUpdated()
        );
    }
}