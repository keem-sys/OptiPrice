package com.optiprice.dto.response;

public record MatchResponse(
        String extracted_new_item_size,
        String extracted_candidate_size,
        String reasoning,
        boolean match,
        String candidate_id
) {}
