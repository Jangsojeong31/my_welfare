package com.welfare.welfare.api;

import com.welfare.security.JwtAuthenticationFilter;
import com.welfare.security.JwtTokenProvider;
import com.welfare.user.application.PersonalizedWelfareService;
import com.welfare.welfare.api.dto.WelfareListResponse;
import com.welfare.welfare.application.WelfareQueryService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WelfareController.class)
@AutoConfigureMockMvc(addFilters = false)
class WelfareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WelfareQueryService welfareQueryService;

    @MockitoBean
    private PersonalizedWelfareService personalizedWelfareService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Mock Service가 빈 응답을 반환하도록 설정
    @BeforeEach
    void setUp() {
        WelfareListResponse emptyResponse = WelfareListResponse.builder()
                .content(List.of())
                .page(0)
                .size(20)
                .totalElements(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .build();

        when(welfareQueryService.search(any(), any(), any(), any(Pageable.class)))
                .thenReturn(emptyResponse);
    }

    @DisplayName("조건 없이 복지 목록을 조회한다.")
    @Test
    void getWelfaresWithoutConditions() throws Exception {
        // when, then
        mockMvc.perform(get("/api/welfare")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());


        // search(null, null, null, pageable)가 실제로 호출되었는지 확인
        verify(welfareQueryService).search(
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }

    @DisplayName("생애주기 조건으로 복지 목록을 조회한다.")
    @Test
    void getWelfaresByLifeStage() throws Exception {
        mockMvc.perform(get("/api/welfare")
                        .param("lifeStages", "05")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());

        verify(welfareQueryService).search(
                eq(List.of("05")),
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }

    @DisplayName("가구 형태 조건으로 복지 목록을 조회한다.")
    @Test
    void getWelfaresByHouseholdType() throws Exception {
        mockMvc.perform(get("/api/welfare")
                        .param("householdTypes", "한부모")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());

        verify(welfareQueryService).search(
                isNull(),
                eq(List.of("한부모")),
                isNull(),
                any(Pageable.class)
        );
    }

    @DisplayName("복수 조건으로 복지 목록을 조회한다.")
    @Test
    void getWelfaresByMultipleConditions() throws Exception {
        mockMvc.perform(get("/api/welfare")
                        .param("lifeStages", "청년", "중장년")
                        .param("householdTypes", "다문화")
                        .param("interests", "고용", "주거")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());

        verify(welfareQueryService).search(
                eq(List.of("청년", "중장년")),
                eq(List.of("다문화")),
                eq(List.of("고용", "주거")),
                any(Pageable.class)
        );
    }
}
