package com.welfare.ai.application;

import com.welfare.ai.api.dto.WelfareAiResponse;
import com.welfare.ai.api.dto.WelfareAiSearchCondition;
import com.welfare.welfare.domain.WelfareService;
import com.welfare.welfare.infrastructure.WelfareServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WelfareAiSearchService {

    private static final int SEARCH_LIMIT = 20;
    private static final int ANSWER_LIMIT = 3;

    private final OpenAiService openAiService;
    private final WelfareServiceRepository welfareServiceRepository;

    public WelfareAiResponse search(String question) {
        // 1. 자연어 분석
        WelfareAiSearchCondition condition =
                openAiService.extractSearchCondition(question);

        // 2. 검색 조건을 이용하여 DB 검색
        List<WelfareService> services =
                searchWelfare(condition);

        // 3. 검색 결과를 AI에게 전달
        String aiAnswer =
                openAiService.generateAnswer(
                        question,
                        condition,
                        services.stream().limit(ANSWER_LIMIT).toList()
                );

        // 4. 최종 응답
        return WelfareAiResponse.of(
                condition,
                services,
                aiAnswer
        );
    }

    private List<WelfareService> searchWelfare(
            WelfareAiSearchCondition condition
    ) {
        Set<WelfareService> result = new LinkedHashSet<>();

        if (condition.getKeywords() != null) {
            for (String keyword : condition.getKeywords()) {
                result.addAll(welfareServiceRepository.searchByKeyword(keyword));
            }
        }

        return result.stream()
                .sorted(Comparator.comparingInt(
                        (WelfareService service) -> relevanceScore(service, condition)
                ).reversed())
                .limit(SEARCH_LIMIT)
                .toList();
    }

    /** * 검색 결과의 관련성 점수 계산 (정렬 목적) */
    private int relevanceScore(WelfareService service, WelfareAiSearchCondition condition) {
        int score = 0;

        if (condition.getKeywords() != null) {
            for (String keyword : condition.getKeywords()) {
                if (contains(service.getServNm(), keyword)) {
                    score += 3;
                }
                if (contains(service.getServDgst(), keyword)) {
                    score += 1;
                }
                if (contains(service.getTgtrDtlCn(), keyword)) {
                    score += 1;
                }
            }
        }

        if (contains(service.getCtpvNm(), condition.getRegion())
                || contains(service.getSggNm(), condition.getRegion())) {
            score += 2;
        }
        if (contains(service.getServDgst(), condition.getEmployment())
                || contains(service.getTgtrDtlCn(), condition.getEmployment())) {
            score += 1;
        }
        if (contains(service.getServDgst(), condition.getHousing())
                || contains(service.getTgtrDtlCn(), condition.getHousing())) {
            score += 1;
        }
        if (contains(service.getServDgst(), condition.getIncome())
                || contains(service.getTgtrDtlCn(), condition.getIncome())) {
            score += 1;
        }

        return score;
    }

    private boolean contains(String text, String keyword) {
        if (!StringUtils.hasText(text) || !StringUtils.hasText(keyword)) {
            return false;
        }
        return text.toLowerCase().contains(keyword.toLowerCase().trim());
    }
}
