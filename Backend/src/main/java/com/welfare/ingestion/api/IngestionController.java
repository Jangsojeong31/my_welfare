package com.welfare.ingestion.api;

import com.welfare.common.api.dto.ApiResponse;
import com.welfare.ingestion.api.dto.IngestionBatchResponse;
import com.welfare.ingestion.api.dto.IngestionResultResponse;
import com.welfare.ingestion.application.WelfareIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ingestion")
@Tag(name = "복지 데이터 수집", description = "공공 Open API를 통한 복지 서비스 데이터 수집")
public class IngestionController {

    private final WelfareIngestionService welfareIngestionService;

    /** 중앙부처·지자체 복지 Open API를 모두 수집한다. */
    @Operation(summary = "복지 데이터 전체 수집", description = "중앙부처와 지자체 복지 Open API를 모두 호출해 신규 데이터를 저장합니다.")
    @PostMapping("/welfare")
    public ApiResponse<IngestionBatchResponse> ingestAll(
            @Parameter(description = "페이지 번호. 생략하면 설정값(기본 1)을 사용합니다.", example = "1")
            @RequestParam(required = false) @Min(1) Integer pageNo,
            @Parameter(description = "한 페이지 결과 수. 생략하면 설정값을 사용합니다. 최대 500.", example = "100")
            @RequestParam(required = false) @Min(1) @Max(500) Integer numOfRows
    ) {
        return ApiResponse.ok(welfareIngestionService.ingestAll(pageNo, numOfRows), "복지 Open API 수집이 완료되었습니다.");
    }

    /** 중앙부처 복지 목록/상세 Open API를 수집한다. */
    @Operation(summary = "중앙부처 복지 수집", description = "중앙부처 복지 목록을 조회한 뒤, 신규 건만 상세 조회하여 저장합니다.")
    @PostMapping("/welfare/national")
    public ApiResponse<IngestionResultResponse> ingestNational(
            @Parameter(description = "페이지 번호. 생략하면 설정값(기본 1)을 사용합니다.", example = "1")
            @RequestParam(required = false) @Min(1) Integer pageNo,
            @Parameter(description = "한 페이지 결과 수. 생략하면 설정값을 사용합니다. 최대 500.", example = "100")
            @RequestParam(required = false) @Min(1) @Max(500) Integer numOfRows
    ) {
        return ApiResponse.ok(welfareIngestionService.ingestNationalWelfare(pageNo, numOfRows), "중앙부처 복지 수집이 완료되었습니다.");
    }

    /** 지자체 복지 목록/상세 Open API를 수집한다. */
    @Operation(summary = "지자체 복지 수집", description = "지자체 복지 목록을 조회한 뒤, 신규 건만 상세 조회하여 저장합니다.")
    @PostMapping("/welfare/local")
    public ApiResponse<IngestionResultResponse> ingestLocal(
            @Parameter(description = "페이지 번호. 생략하면 설정값(기본 1)을 사용합니다.", example = "1")
            @RequestParam(required = false) @Min(1) Integer pageNo,
            @Parameter(description = "한 페이지 결과 수. 생략하면 설정값을 사용합니다. 최대 500.", example = "100")
            @RequestParam(required = false) @Min(1) @Max(500) Integer numOfRows
    ) {
        return ApiResponse.ok(welfareIngestionService.ingestLocalWelfare(pageNo, numOfRows), "지자체 복지 수집이 완료되었습니다.");
    }
}
