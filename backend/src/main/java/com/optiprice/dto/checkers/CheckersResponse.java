package com.optiprice.dto.checkers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheckersResponse(
        @JsonProperty("products")
        List<CheckersProduct> products,

        @JsonProperty("totalCount")
        Integer totalCount,

        @JsonProperty("success")
        Boolean success
) {}