package com.optiprice.dto.pnp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PnpImage(
        String url,
        String format,
        String imageType
) {}