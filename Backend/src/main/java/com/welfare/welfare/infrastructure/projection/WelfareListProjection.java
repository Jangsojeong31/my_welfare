package com.welfare.welfare.infrastructure.projection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 복지 목록 조회용 컬럼. 생성자 순서가 SELECT 순서와 같아야 한다.
 */
@Getter
@RequiredArgsConstructor
public class WelfareListProjection {

    private final String id;
    private final String servNm;
    private final String servDgst;
    private final String onapPsbltYn;
    private final String ctpvNm;
    private final String sggNm;
    private final String wlfareInfoOutlCn;
}
