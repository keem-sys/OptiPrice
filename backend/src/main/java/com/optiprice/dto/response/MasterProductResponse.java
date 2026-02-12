package com.optiprice.dto.response;

import java.util.List;

public record MasterProductResponse(
        Long id,
        String genericName,
        String category,
        List<StoreItemResponse> storeItems
) {}