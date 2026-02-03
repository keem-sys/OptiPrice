package com.optiprice.dto.pnp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PnpProduct(
        String code,
        String name,
        Boolean inStockIndicator,
        PnpPrice price,
        List<PnpImage> images
) {}