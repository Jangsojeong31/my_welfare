package com.welfare.ingestion.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "복지 Open API 수집 결과")
public class IngestionResultResponse {

    @Schema(description = "목록 조회 API 코드")
    private final String listApiCd;

    @Schema(description = "상세 조회 API 코드")
    private final String detailApiCd;

    @Schema(description = "조회된 건수")
    private final int fetchedCount;

    @Schema(description = "신규 저장 건수")
    private final int insertedCount;

    @Schema(description = "중복으로 건너뛴 건수")
    private final int skippedCount;

    @Schema(description = "저장 실패 건수")
    private final int failedCount;
}
