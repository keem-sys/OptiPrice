package com.optiprice.dto.response;

public record StoreResponse(
        Long id,
        String name,
        String logoUrl,
        String websiteUrl
) {}