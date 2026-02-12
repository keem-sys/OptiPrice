package com.optiprice.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record StoreItemResponse(
        Long id,
        String store,
        String storeLogoUrl,
        String storeSpecificName,
        BigDecimal price,
        String productUrl,
        String imageUrl,
        OffsetDateTime lastUpdated
) {}