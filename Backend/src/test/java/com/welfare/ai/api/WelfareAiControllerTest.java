package com.welfare.ai.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.welfare.ai.api.dto.WelfareAiRecommendation;
import com.welfare.ai.api.dto.WelfareAiResponse;
import com.welfare.ai.api.dto.WelfareAiSearchCondition;
import com.welfare.ai.api.dto.WelfareAiSearchRequest;
import com.welfare.ai.application.WelfareAiSearchService;
import com.welfare.security.JwtAuthenticationFilter;
import com.welfare.security.JwtTokenProvider;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WelfareAiController.class)
@AutoConfigureMockMvc(addFilters = false)
class WelfareAiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WelfareAiSearchService welfareAiSearchService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @DisplayName("자연어 질문으로 복지 서비스를 검색한다.")
    @Test
    void searchWelfare() throws Exception {
        // given
        WelfareAiSearchRequest request = new WelfareAiSearchRequest("서울에 사는 청년인데 월세 지원을 찾고 있어요");

        WelfareAiResponse response = WelfareAiResponse.builder()
                .condition(WelfareAiSearchCondition.builder()
                        .age(28)
                        .region("서울")
                        .housing("월세")
                        .income("차상위")
                        .keywords(List.of("청년", "주거"))
                        .build())
                .recommendations(List.of(WelfareAiRecommendation.builder()
                        .id("welfare-1")
                        .servId("W001")
                        .servNm("청년 월세 지원")
                        .summary("청년 월세를 지원합니다.")
                        .detailLink("https://example.com/welfare/W001")
                        .reason("주거 관심분야와 일치합니다.")
                        .build()))
                .answer("청년 월세 지원을 추천합니다.")
                .build();

        when(welfareAiSearchService.search(request.getQuestion())).thenReturn(response);

        // when, then
        mockMvc.perform(post("/api/ai/welfare/search")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("청년 월세 지원을 추천합니다."))
                .andExpect(jsonPath("$.condition.age").value(28))
                .andExpect(jsonPath("$.condition.region").value("서울"))
                .andExpect(jsonPath("$.condition.housing").value("월세"))
                .andExpect(jsonPath("$.condition.income").value("차상위"))
                .andExpect(jsonPath("$.condition.keywords[0]").value("청년"))
                .andExpect(jsonPath("$.condition.keywords[1]").value("주거"))
                .andExpect(jsonPath("$.recommendations[0].id").value("welfare-1"))
                .andExpect(jsonPath("$.recommendations[0].servId").value("W001"))
                .andExpect(jsonPath("$.recommendations[0].servNm").value("청년 월세 지원"))
                .andExpect(jsonPath("$.recommendations[0].summary").value("청년 월세를 지원합니다."))
                .andExpect(jsonPath("$.recommendations[0].detailLink").value("https://example.com/welfare/W001"))
                .andExpect(jsonPath("$.recommendations[0].reason").value("주거 관심분야와 일치합니다."));

        verify(welfareAiSearchService).search("서울에 사는 청년인데 월세 지원을 찾고 있어요");
    }

    @DisplayName("AI 복지 검색 시 질문은 필수값이다.")
    @Test
    void searchWithoutQuestion() throws Exception {
        // given
        WelfareAiSearchRequest request = new WelfareAiSearchRequest(" ");

        // when, then
        mockMvc.perform(post("/api/ai/welfare/search")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("question: 질문은 필수입니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));

        verifyNoInteractions(welfareAiSearchService);
    }
}
