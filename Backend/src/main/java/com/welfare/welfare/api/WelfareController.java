package com.welfare.welfare.api;

import com.welfare.common.api.dto.ApiResponse;
import com.welfare.welfare.api.dto.WelfareDetailResponse;
import com.welfare.welfare.api.dto.WelfareListResponse;
import com.welfare.welfare.application.WelfareQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/welfare")
@Tag(name = "복지 서비스", description = "복지 서비스 목록 및 상세 조회")
public class WelfareController {

    private final WelfareQueryService welfareQueryService;

    /** 생애주기, 가구형태, 관심분야 조건으로 복지 서비스 목록을 조회한다. */
    @Operation(
            summary = "복지 목록 조회",
            description = """
                    복지 서비스 목록을 조회합니다. 조건이 없으면 전체 목록을 반환합니다.
                    같은 조건은 OR, 서로 다른 조건은 AND로 적용됩니다.
                    코드 또는 이름을 사용할 수 있으며, 쉼표로 여러 값을 전달할 수 있습니다.
                    """
    )
    @SecurityRequirements
    @GetMapping
    public ApiResponse<WelfareListResponse> search(
            @Parameter(description = "생애주기 코드 또는 이름", example = "05")
            @RequestParam(required = false) List<String> lifeStages,
            @Parameter(description = "가구형태 코드 또는 이름", example = "05")
            @RequestParam(required = false) List<String> householdTypes,
            @Parameter(description = "관심분야 코드 또는 이름", example = "05")
            @RequestParam(required = false) List<String> interests,
            @ParameterObject
            @PageableDefault(size = 20, sort = "inqNum", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.ok(welfareQueryService.search(lifeStages, householdTypes, interests, pageable));
    }

    /** 복지 서비스 상세 정보를 조회한다. */
    @SecurityRequirements
    @Operation(summary = "복지 상세 조회", description = "복지서비스 ID로 상세 정보와 신청 방법, 문의처, 관련 링크/서식/법령을 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<WelfareDetailResponse> getDetail(
            @Parameter(description = "복지서비스 ID")
            @PathVariable String id
    ) {
        return ApiResponse.ok(welfareQueryService.getDetail(id));
    }
}
