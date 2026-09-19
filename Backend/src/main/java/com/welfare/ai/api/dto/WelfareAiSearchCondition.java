package com.welfare.ai.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "AI 검색 조건")
public class WelfareAiSearchCondition {

    private Integer age;

    private String region;

    private String employment;

    private String housing;

    private String income;

    private List<String> keywords;
}
