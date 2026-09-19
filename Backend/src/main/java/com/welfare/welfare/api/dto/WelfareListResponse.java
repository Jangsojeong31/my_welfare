package com.welfare.welfare.api.dto;

import com.welfare.welfare.domain.WelfareService;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
@Schema(description = "복지 서비스 목록 조회 결과")
public class WelfareListResponse {

    @Schema(description = "목록")
    private final List<WelfareListItemResponse> content;

    @Schema(description = "현재 페이지 번호 (0부터 시작)")
    private final int page;

    @Schema(description = "페이지 크기")
    private final int size;

    @Schema(description = "전체 건수")
    private final long totalElements;

    @Schema(description = "전체 페이지 수")
    private final int totalPages;

    @Schema(description = "첫 페이지 여부")
    private final boolean first;

    @Schema(description = "마지막 페이지 여부")
    private final boolean last;

    public static WelfareListResponse from(Page<WelfareService> page) {
        return WelfareListResponse.builder()
                .content(page.getContent().stream().map(WelfareListItemResponse::from).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
