package com.optiprice.dto.response;

public record MatchResponse(
        boolean match,
        String candidate_id,
        String reason
) {}
