package com.optiprice.dto.pnp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PnpProduct(
        String code,
        String name,
        Boolean inStockIndicator,
        PnpPrice price,
        List<PnpImage> images,
        List<PnpPromotion> potentialPromotions,
        String productUrl,
        @JsonProperty("brandSellerId")
        String brand
) {}