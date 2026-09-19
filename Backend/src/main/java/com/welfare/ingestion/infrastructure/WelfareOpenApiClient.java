package com.welfare.ingestion.infrastructure;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.welfare.common.exception.BusinessException;
import com.welfare.common.exception.ErrorCode;
import com.welfare.config.OpenApiProperties;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class WelfareOpenApiClient {

    private final RestClient openApiRestClient;
    private final OpenApiProperties properties;
    private final ObjectMapper objectMapper;
    private final XmlMapper xmlMapper = XmlMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    public OpenApiResponse fetchList(String apiUrl, int pageNo, int numOfRows) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("serviceKey", properties.getServiceKey());
        if (apiUrl != null && apiUrl.contains("NationalWelfarelist")) {
            params.put("callTp", properties.getListCallTp());
            params.put("pageNo", String.valueOf(pageNo));
            params.put("numOfRows", String.valueOf(numOfRows));
            params.put("srchKeyCode", "001");
        }
        return execute(apiUrl, params);
    }

    public OpenApiResponse fetchDetail(String apiUrl, String servCd) {
        String resolvedServCd = OpenApiNodeUtils.blankToNull(servCd);
        if (resolvedServCd == null) {
            resolvedServCd = properties.getDetailServCd();
        }
        Map<String, String> params = new LinkedHashMap<>();
        params.put("serviceKey", properties.getServiceKey());
//        params.put("servCd", resolvedServCd);
        params.put("servId", resolvedServCd);
        if (apiUrl != null && apiUrl.contains("NationalWelfareDetail")) {
            params.put("callTp", "D");
        }
        return execute(apiUrl, params);
    }

    private OpenApiResponse execute(String apiUrl, Map<String, String> params) {
        URI uri = buildUri(apiUrl, params);
        try {
            ResponseEntity<String> response = openApiRestClient.get()
                    .uri(uri)
                    .retrieve()
                    .toEntity(String.class);
            String body = response.getBody() == null ? "" : response.getBody();
            JsonNode root = parseBody(body);
            return toOpenApiResponse(uri.toString(), params, response.getStatusCode().value(), body, root);
        } catch (RestClientException e) {
            log.error("Open API 호출 실패 url={}", uri, e);
            throw new BusinessException(ErrorCode.OPEN_API_CALL_FAILED, e.getMessage());
        }
    }

    private OpenApiResponse toOpenApiResponse(
            String requestUrl,
            Map<String, String> params,
            int httpStatus,
            String body,
            JsonNode root
    ) {
        String resultCode = firstText(root, "resultCode", "returnReasonCode");
        String resultMessage = firstText(root, "resultMessage", "resultMsg", "errMsg", "returnAuthMsg", "returnReasonMsg");
        Integer pageNo = integerFromTree(root, "pageNo");
        Integer numOfRows = integerFromTree(root, "numOfRows");
        Integer totalCount = integerFromTree(root, "totalCount");
        boolean success = httpStatus >= 200 && httpStatus < 300 && isSuccessCode(resultCode) && !isAuthError(root);
        List<JsonNode> items = extractItems(root);
        return new OpenApiResponse(
                requestUrl,
                params,
                httpStatus,
                body,
                root,
                success,
                resultCode,
                resultMessage,
                pageNo,
                numOfRows,
                totalCount,
                items
        );
    }

    private List<JsonNode> extractItems(JsonNode root) {
        JsonNode servList = OpenApiNodeUtils.findFirst(root, "servList");
        if (servList != null) {
            return OpenApiNodeUtils.asList(servList);
        }
        JsonNode wantedDtl = OpenApiNodeUtils.findFirst(root, "wantedDtl", "servDetail", "welfareDetail");
        if (wantedDtl != null) {
            return OpenApiNodeUtils.asList(wantedDtl);
        }
        JsonNode item = OpenApiNodeUtils.findFirst(root, "item", "items");
        if (item != null) {
            JsonNode nestedItem = OpenApiNodeUtils.child(item, "item");
            return OpenApiNodeUtils.asList(nestedItem != null ? nestedItem : item);
        }
        if (root != null && (OpenApiNodeUtils.text(root, "servId", "servNm", "복지 서비스 코드", "복지 서비스명") != null)) {
            return List.of(root);
        }
        return new ArrayList<>();
    }

    private JsonNode parseBody(String body) {
        String trimmed = body == null ? "" : body.trim();
        try {
            if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                return objectMapper.readTree(trimmed);
            }
            return xmlMapper.readTree(trimmed);
        } catch (Exception e) {
            log.warn("Open API 응답 파싱 실패: {}", e.getMessage());
            return objectMapper.createObjectNode();
        }
    }

    private boolean isSuccessCode(String resultCode) {
        if (resultCode == null) {
            return true;
        }
        return "0".equals(resultCode) || "00".equals(resultCode) || "000".equals(resultCode) || "0000".equals(resultCode);
    }

    private boolean isAuthError(JsonNode root) {
        String authMessage = OpenApiNodeUtils.text(root, "returnAuthMsg", "errMsg");
        return authMessage != null && authMessage.toUpperCase().contains("ERROR");
    }

    private URI buildUri(String baseUrl, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(baseUrl);
        builder.append(baseUrl.contains("?") ? "&" : "?");
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                builder.append("&");
            }
            first = false;
            builder.append(encode(entry.getKey()))
                    .append("=")
                    .append(encode(entry.getValue()));
        }
        return URI.create(builder.toString());
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String firstText(JsonNode root, String... keys) {
        JsonNode node = OpenApiNodeUtils.findFirst(root, keys);
        return OpenApiNodeUtils.nodeToString(node);
    }

    private Integer integerFromTree(JsonNode root, String key) {
        JsonNode node = OpenApiNodeUtils.findFirst(root, key);
        return OpenApiNodeUtils.integerOrNull(node);
    }
}
