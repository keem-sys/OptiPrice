package com.optiprice.dto.pnp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PnpPromotion (
    String promotionTextMessage,
    String promotionDisplayType,
    Boolean valid
) {}
