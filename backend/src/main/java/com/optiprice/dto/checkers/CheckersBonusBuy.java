package com.optiprice.dto.checkers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheckersBonusBuy(
        String code,
        String name,
        String shortDescription,
        Double discountValue,
        CheckersDiscountType discountType,
        CheckersMemberType memberType
)
{}
