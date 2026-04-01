package com.optiprice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface BasketTrendProjection {
    LocalDate getLogDate();
    String getStoreName();
    BigDecimal getBasketPrice();
}