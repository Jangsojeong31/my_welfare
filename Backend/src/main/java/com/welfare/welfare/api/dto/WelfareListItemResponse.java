package com.welfare.welfare.api.dto;

import com.welfare.welfare.domain.WelfareService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "복지 서비스 목록 항목")
public class WelfareListItemResponse {

    @Schema(description = "복지서비스 ID")
    private final String id;

    @Schema(description = "복지서비스명")
    private final String servNm;

    @Schema(description = "서비스 요약")
    private final String servDgst;

    @Schema(description = "온라인 신청 가능 여부 (Y/N)")
    private final String onapPsbltYn;

    @Schema(description = "시도명")
    private final String ctpvNm;

    @Schema(description = "시군구명")
    private final String sggNm;

    @Schema(description = "복지서비스 개요")
    private final String wlfareInfoOutlCn;

    public static WelfareListItemResponse from(WelfareService service) {
        return WelfareListItemResponse.builder()
                .id(service.getId())
                .servNm(service.getServNm())
                .servDgst(service.getServDgst())
                .onapPsbltYn(service.getOnapPsbltYn())
                .ctpvNm(service.getCtpvNm())
                .sggNm(service.getSggNm())
                .wlfareInfoOutlCn(service.getWlfareInfoOutlCn())
                .build();
    }
}
