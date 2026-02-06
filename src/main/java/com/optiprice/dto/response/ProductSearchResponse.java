package com.optiprice.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ProductSearchResponse(
        String storeName,
        String storeLogoUrl,
        String productName,
        BigDecimal price,
        String brand,
        String imageUrl,
        String productLink,
        OffsetDateTime lastUpdated
) {}