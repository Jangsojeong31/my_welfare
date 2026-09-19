package com.welfare.ai.api;

import com.welfare.ai.api.dto.WelfareAiResponse;
import com.welfare.ai.api.dto.WelfareAiSearchRequest;
import com.welfare.ai.application.WelfareAiSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/welfare")
@Tag(name = "AI 복지 검색", description = "AI를 통한 복지 서비스 검색")
public class WelfareAiController {

    private final WelfareAiSearchService welfareAiSearchService;

    /** 자연어 질문으로 복지 서비스를 검색하고 AI 설명을 반환한다. */
    @Operation(
            summary = "AI 복지 검색",
            description = """
                    자연어 질문을 분석해 검색 조건을 추출하고, 관련 복지 서비스를 찾아 AI 설명을 반환합니다.
                    """
    )
    @PostMapping("/search")
    public WelfareAiResponse search(
            @RequestBody WelfareAiSearchRequest request
            ) {
        return welfareAiSearchService.search(request.getQuestion());
    }
}
