package com.welfare.ingestion.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.welfare.common.exception.BusinessException;
import com.welfare.common.exception.ErrorCode;
import com.welfare.config.OpenApiProperties;
import com.welfare.ingestion.api.dto.IngestionBatchResponse;
import com.welfare.ingestion.api.dto.IngestionResultResponse;
import com.welfare.ingestion.domain.ApiCollectionHistory;
import com.welfare.ingestion.infrastructure.ApiCollectionHistoryRepository;
import com.welfare.ingestion.infrastructure.OpenApiResponse;
import com.welfare.ingestion.infrastructure.WelfareOpenApiClient;
import com.welfare.welfare.domain.WelfareApi;
import com.welfare.welfare.domain.WelfareService;
import com.welfare.welfare.infrastructure.WelfareApiRepository;
import com.welfare.welfare.infrastructure.WelfareServiceRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WelfareIngestionService {

    private static final String API_TYPE_LIST = "LIST";
    private static final String API_TYPE_DETAIL = "DETAIL";

    private final OpenApiProperties openApiProperties;
    private final WelfareApiRepository welfareApiRepository;
    private final WelfareServiceRepository welfareServiceRepository;
    private final ApiCollectionHistoryRepository apiCollectionHistoryRepository;
    private final WelfareOpenApiClient welfareOpenApiClient;
    private final WelfareOpenApiMapper welfareOpenApiMapper;
    private final WelfareCodeMatcher welfareCodeMatcher;

    public IngestionBatchResponse ingestAll(Integer pageNo, Integer numOfRows) {
        return IngestionBatchResponse.builder()
                .national(ingestNationalWelfare(pageNo, numOfRows))
                .local(ingestLocalWelfare(pageNo, numOfRows))
                .build();
    }

    public IngestionResultResponse ingestNationalWelfare(Integer pageNo, Integer numOfRows) {
        return ingest(
                openApiProperties.getNationalListApiCd(),
                openApiProperties.getNationalDetailApiCd(),
                pageNo,
                numOfRows
        );
    }

    public IngestionResultResponse ingestLocalWelfare(Integer pageNo, Integer numOfRows) {
        return ingest(
                openApiProperties.getLocalListApiCd(),
                openApiProperties.getLocalDetailApiCd(),
                pageNo,
                numOfRows
        );
    }

    private IngestionResultResponse ingest(String listApiCd, String detailApiCd, Integer pageNo, Integer numOfRows) {
        WelfareApi listApi = getApi(listApiCd);
        WelfareApi detailApi = getApi(detailApiCd);

        int resolvedPageNo = pageNo != null ? pageNo : openApiProperties.getPageNo();
        int resolvedNumOfRows = numOfRows != null ? numOfRows : openApiProperties.getNumOfRows();
        int maxPages = openApiProperties.getMaxPages();
        int fetchedPages = 0;
        int fetchedCount = 0;
        int insertedCount = 0;
        int skippedCount = 0;
        int failedCount = 0;
        Integer totalCount = null;

        OpenApiResponse listResponse = welfareOpenApiClient.fetchList(listApi.getApiUrl(), resolvedPageNo, resolvedNumOfRows);
        saveHistory(listApiCd, API_TYPE_LIST, listResponse);
//        fetchedPages++;
        if (!listResponse.success()) {
            throw new BusinessException(
                    ErrorCode.OPEN_API_CALL_FAILED,
                    listResponse.resultMessage() == null ? "목록 조회 API 호출에 실패했습니다." : listResponse.resultMessage()
            );
        }

        List<JsonNode> items = listResponse.items();
        fetchedCount += items.size();
        if (listResponse.totalCount() != null) {
            totalCount = listResponse.totalCount();
        }

        for (JsonNode item : items) {
            try {
                boolean inserted = saveIfNew(listApiCd, detailApi, item);
                if (inserted) {
                    insertedCount++;
                } else {
                    skippedCount++;
                }
            } catch (Exception e) {
                failedCount++;
                log.warn("복지 데이터 저장 실패 apiCd={}, servCd={}", listApiCd, welfareOpenApiMapper.extractServCd(item), e);
            }
        }

//        if (items.isEmpty() || items.size() < numOfRows) {
//            break;
//        }
//        if (totalCount != null && pageNo * numOfRows >= totalCount) {
//            break;
//        }

//        while (fetchedPages < maxPages) {
//        }

        return IngestionResultResponse.builder()
                .listApiCd(listApiCd)
                .detailApiCd(detailApiCd)
                .fetchedCount(fetchedCount)
                .insertedCount(insertedCount)
                .skippedCount(skippedCount)
                .failedCount(failedCount)
                .build();
    }

    private boolean saveIfNew(String listApiCd, WelfareApi detailApi, JsonNode listNode) {
        String servCd = welfareOpenApiMapper.extractServCd(listNode);
        if (servCd == null) {
            return false;
        }
        if (welfareServiceRepository.existsByApiCdAndServCd(listApiCd, servCd)) {
            return false;
        }

        WelfareService service = welfareOpenApiMapper.toNewService(listApiCd, listNode);
        if (service == null) {
            return false;
        }
        applyCodes(service, listNode);

        try {
            OpenApiResponse detailResponse = welfareOpenApiClient.fetchDetail(detailApi.getApiUrl(), servCd);
            saveHistory(detailApi.getApiCd(), API_TYPE_DETAIL, detailResponse);
            if (detailResponse.success()) {
                JsonNode detailNode = detailResponse.items().isEmpty() ? detailResponse.root() : detailResponse.items().get(0);
                welfareOpenApiMapper.applyDetail(service, detailNode);
                welfareOpenApiMapper.mapDetailChildren(service, detailNode);
                applyCodes(service, detailNode);
            } else {
                log.warn("상세 조회 실패 apiCd={}, servCd={}, message={}",
                        detailApi.getApiCd(), servCd, detailResponse.resultMessage());
            }
        } catch (Exception e) {
            log.warn("상세 조회 중 예외 발생 apiCd={}, servCd={}", detailApi.getApiCd(), servCd, e);
        }

        welfareServiceRepository.save(service);
        return true;
    }

    private void applyCodes(WelfareService service, JsonNode node) {
        List<String> lifeStages = welfareOpenApiMapper.extractLifeStageValues(node);
        if (!lifeStages.isEmpty()) {
            service.replaceLifeStages(welfareCodeMatcher.matchLifeStages(lifeStages));
        }
        List<String> householdTypes = welfareOpenApiMapper.extractHouseholdTypeValues(node);
        if (!householdTypes.isEmpty()) {
            service.replaceHouseholdTypes(welfareCodeMatcher.matchHouseholdTypes(householdTypes));
        }
        List<String> interests = welfareOpenApiMapper.extractInterestValues(node);
        if (!interests.isEmpty()) {
            service.replaceInterests(welfareCodeMatcher.matchInterests(interests));
        }
    }

    private WelfareApi getApi(String apiCd) {
        return welfareApiRepository.findByApiCd(apiCd)
                .orElseThrow(() -> new BusinessException(ErrorCode.WELFARE_API_NOT_FOUND, "API 코드가 없습니다: " + apiCd));
    }

    private void saveHistory(String apiCd, String apiType, OpenApiResponse response) {
        apiCollectionHistoryRepository.save(ApiCollectionHistory.builder()
                .apiCd(apiCd)
                .apiType(apiType)
                .requestUrl(response.requestUrl())
                .requestParams(response.requestParams())
                .responseCode(response.httpStatus())
                .responseMessage(response.resultMessage())
                .pageNo(response.pageNo())
                .numOfRows(response.numOfRows())
                .totalCount(response.totalCount())
                .successYn(response.success() ? "Y" : "N")
                .startedAt(LocalDateTime.now())
                .finishedAt(LocalDateTime.now())
                .build());
    }
}
