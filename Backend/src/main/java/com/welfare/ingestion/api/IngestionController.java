package com.welfare.ingestion.api;

import com.welfare.common.api.dto.ApiResponse;
import com.welfare.ingestion.api.dto.IngestionBatchResponse;
import com.welfare.ingestion.api.dto.IngestionResultResponse;
import com.welfare.ingestion.application.WelfareIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ingestion")
@Tag(name = "복지 데이터 수집", description = "공공 Open API를 통한 복지 서비스 데이터 수집")
public class IngestionController {

    private final WelfareIngestionService welfareIngestionService;

    /** 중앙부처·지자체 복지 Open API를 모두 수집한다. */
    @Operation(summary = "복지 데이터 전체 수집", description = "중앙부처와 지자체 복지 Open API를 모두 호출해 신규 데이터를 저장합니다.")
    @PostMapping("/welfare")
    public ApiResponse<IngestionBatchResponse> ingestAll() {
        return ApiResponse.ok(welfareIngestionService.ingestAll(), "복지 Open API 수집이 완료되었습니다.");
    }

    /** 중앙부처 복지 목록/상세 Open API를 수집한다. */
    @Operation(summary = "중앙부처 복지 수집", description = "중앙부처 복지 목록을 조회한 뒤, 신규 건만 상세 조회하여 저장합니다.")
    @PostMapping("/welfare/national")
    public ApiResponse<IngestionResultResponse> ingestNational() {
        return ApiResponse.ok(welfareIngestionService.ingestNationalWelfare(), "중앙부처 복지 수집이 완료되었습니다.");
    }

    /** 지자체 복지 목록/상세 Open API를 수집한다. */
    @Operation(summary = "지자체 복지 수집", description = "지자체 복지 목록을 조회한 뒤, 신규 건만 상세 조회하여 저장합니다.")
    @PostMapping("/welfare/local")
    public ApiResponse<IngestionResultResponse> ingestLocal() {
        return ApiResponse.ok(welfareIngestionService.ingestLocalWelfare(), "지자체 복지 수집이 완료되었습니다.");
    }
}
