package com.welfare.ai.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "AI 검색 결과")
public class WelfareAiRecommendation {

    @Schema(description = "복지서비스 ID")
    private String id;

    @Schema(description = "복지서비스 코드")
    private String servId;

    private String servNm;

    private String summary;

    private String detailLink;

    private String reason;
}
