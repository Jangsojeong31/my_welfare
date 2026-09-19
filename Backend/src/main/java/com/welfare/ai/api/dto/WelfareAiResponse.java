package com.welfare.ai.api.dto;

import com.welfare.welfare.domain.WelfareService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "AI 검색 결과 전체 응답")
public class WelfareAiResponse {

    private WelfareAiSearchCondition condition;

    private List<WelfareAiRecommendation> recommendations;

    private String answer;

    public static WelfareAiResponse of(
            WelfareAiSearchCondition condition, List<WelfareService> services, String answer
    ) {
        List<WelfareAiRecommendation> recommendations
                = services.stream()
                .map(service ->
                        WelfareAiRecommendation.builder()
                                .id(service.getId())
                                .servId(service.getServCd())
                                .servNm(service.getServNm())
                                .summary(service.getServDgst())
                                .detailLink(service.getServDtlLink())
                                .reason(null) .build() )
                .toList();

        return WelfareAiResponse.builder()
                .condition(condition)
                .recommendations(recommendations)
                .answer(answer) .build();
    }

}
