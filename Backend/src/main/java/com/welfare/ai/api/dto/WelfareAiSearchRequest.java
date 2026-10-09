package com.welfare.ai.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "AI 검색 요청")
public class WelfareAiSearchRequest {

    @NotBlank(message = "질문은 필수입니다.")
    private String question;

    public WelfareAiSearchRequest(String question) {
        this.question = question;
    }
}
