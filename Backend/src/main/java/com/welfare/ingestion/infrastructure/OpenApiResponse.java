package com.welfare.ingestion.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;

public record OpenApiResponse(
        String requestUrl,
        Map<String, String> requestParams,
        int httpStatus,
        String rawBody,
        JsonNode root,
        boolean success,
        String resultCode,
        String resultMessage,
        Integer pageNo,
        Integer numOfRows,
        Integer totalCount,
        List<JsonNode> items
) {
}
