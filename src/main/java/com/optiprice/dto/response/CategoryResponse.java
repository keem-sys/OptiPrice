package com.optiprice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryResponse(
        @JsonProperty("category")
        String category
) {}