package com.optiprice.dto.pnp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PnpPrice(
        Double value,
        String currencyIso,
        String formattedValue
) {}