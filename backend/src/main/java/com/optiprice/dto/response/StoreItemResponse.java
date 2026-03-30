package com.optiprice.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record StoreItemResponse(
        Long id,
        StoreResponse store,
        String brand,
        String storeSpecificName,
        BigDecimal price,
        BigDecimal oldPrice,
        Boolean isOnPromotion,
        String promotionText,
        String barcode,
        String productUrl,
        String imageUrl,
        OffsetDateTime lastUpdated
) {}