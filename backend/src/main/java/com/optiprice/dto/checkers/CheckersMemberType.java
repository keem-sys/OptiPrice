package com.optiprice.dto.checkers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheckersMemberType(
        String code,
        String name
) {}