package com.optiprice.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PriceHistoryPoint(
        OffsetDateTime date,
        String storeName,
        BigDecimal price
) {}