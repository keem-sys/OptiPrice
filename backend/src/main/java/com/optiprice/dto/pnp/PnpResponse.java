package com.optiprice.dto.pnp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PnpResponse(
        List<PnpProduct> products
) {}