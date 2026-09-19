package com.welfare.ingestion.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "중앙부처·지자체 복지 수집 결과")
public class IngestionBatchResponse {

    @Schema(description = "중앙부처 복지 수집 결과")
    private final IngestionResultResponse national;

    @Schema(description = "지자체 복지 수집 결과")
    private final IngestionResultResponse local;
}
