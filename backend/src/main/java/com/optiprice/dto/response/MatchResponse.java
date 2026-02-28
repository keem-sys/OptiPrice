package com.optiprice.dto.response;

public record MatchResponse(
        String new_item_physical_object,
        String candidate_physical_object,
        String extracted_new_brand,
        String extracted_candidate_brand,
        String extracted_new_item_size,
        String extracted_candidate_size,
        String core_product_type,
        String reasoning,
        String candidate_id,
        boolean match
) {}
