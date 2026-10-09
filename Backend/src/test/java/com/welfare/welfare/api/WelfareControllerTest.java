package com.welfare.welfare.api;

import com.welfare.security.JwtAuthenticationFilter;
import com.welfare.security.JwtTokenProvider;
import com.welfare.user.application.PersonalizedWelfareService;
import com.welfare.welfare.api.dto.WelfareCodeNameResponse;
import com.welfare.welfare.api.dto.WelfareDetailResponse;
import com.welfare.welfare.api.dto.WelfareListItemResponse;
import com.welfare.welfare.api.dto.WelfareListResponse;
import com.welfare.welfare.application.WelfareQueryService;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

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

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
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

    @DisplayName("복지 서비스 상세를 조회한다.")
    @Test
    void getWelfareDetail() throws Exception {
        // given
        WelfareDetailResponse detail = WelfareDetailResponse.builder()
                .id("welfare-1")
                .servCd("W001")
                .servNm("청년 월세 지원")
                .servDgst("청년 월세를 지원합니다.")
                .onapPsbltYn("Y")
                .ctpvNm("서울특별시")
                .sggNm("강남구")
                .lifeStages(List.of(WelfareCodeNameResponse.builder().code("05").name("청년").build()))
                .householdTypes(List.of())
                .interests(List.of(WelfareCodeNameResponse.builder().code("05").name("주거").build()))
                .applications(List.of(WelfareDetailResponse.ApplicationItem.builder()
                        .servSeCode("010")
                        .servSeDetailNm("온라인 신청")
                        .servSeDetailLink("https://example.com/apply")
                        .build()))
                .contacts(List.of())
                .links(List.of())
                .forms(List.of())
                .laws(List.of())
                .build();

        when(welfareQueryService.getDetail("welfare-1")).thenReturn(detail);

        // when, then
        mockMvc.perform(get("/api/welfare/{id}", "welfare-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("welfare-1"))
                .andExpect(jsonPath("$.data.servCd").value("W001"))
                .andExpect(jsonPath("$.data.servNm").value("청년 월세 지원"))
                .andExpect(jsonPath("$.data.servDgst").value("청년 월세를 지원합니다."))
                .andExpect(jsonPath("$.data.onapPsbltYn").value("Y"))
                .andExpect(jsonPath("$.data.ctpvNm").value("서울특별시"))
                .andExpect(jsonPath("$.data.sggNm").value("강남구"))
                .andExpect(jsonPath("$.data.lifeStages[0].code").value("05"))
                .andExpect(jsonPath("$.data.lifeStages[0].name").value("청년"))
                .andExpect(jsonPath("$.data.interests[0].name").value("주거"))
                .andExpect(jsonPath("$.data.applications[0].servSeDetailNm").value("온라인 신청"));

        verify(welfareQueryService).getDetail("welfare-1");
    }

    @DisplayName("복지 서비스 ID가 8자 미만이면 조회할 수 없다.")
    @Test
    void getWelfareDetailWithShortId() throws Exception {
        mockMvc.perform(get("/api/welfare/{id}", "short")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("요청한 경로를 찾을 수 없습니다."))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));

        verifyNoInteractions(welfareQueryService);
    }

    @DisplayName("복지 서비스 ID가 50자를 초과하면 조회할 수 없다.")
    @Test
    void getWelfareDetailWithLongId() throws Exception {
        mockMvc.perform(get("/api/welfare/{id}", "a".repeat(51))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("요청한 경로를 찾을 수 없습니다."))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));

        verifyNoInteractions(welfareQueryService);
    }

    @DisplayName("로그인한 회원의 맞춤 복지 목록을 조회한다.")
    @Test
    void getMyWelfares() throws Exception {
        // given
        WelfareListItemResponse item = WelfareListItemResponse.builder()
                .id("welfare-1")
                .servNm("청년 월세 지원")
                .servDgst("청년 월세를 지원합니다.")
                .onapPsbltYn("Y")
                .ctpvNm("서울특별시")
                .sggNm("강남구")
                .score(90)
                .matchReasons(List.of("생애주기 일치", "관심분야 일치"))
                .build();

        WelfareListResponse response = WelfareListResponse.builder()
                .content(List.of(item))
                .page(0)
                .size(20)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .build();

        when(personalizedWelfareService.search(eq("user@example.com"), any(Pageable.class)))
                .thenReturn(response);

        // when, then
        mockMvc.perform(get("/api/welfare/me")
                        .with(authenticatedUser("user@example.com"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value("welfare-1"))
                .andExpect(jsonPath("$.data.content[0].servNm").value("청년 월세 지원"))
                .andExpect(jsonPath("$.data.content[0].score").value(90))
                .andExpect(jsonPath("$.data.content[0].matchReasons[0]").value("생애주기 일치"))
                .andExpect(jsonPath("$.data.content[0].matchReasons[1]").value("관심분야 일치"));

        verify(personalizedWelfareService).search(eq("user@example.com"), any(Pageable.class));
    }

    private RequestPostProcessor authenticatedUser(String email) {
        return request -> {
            UserDetails userDetails = User.withUsername(email)
                    .password("password")
                    .roles("USER")
                    .build();
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return request;
        };
    }
}
