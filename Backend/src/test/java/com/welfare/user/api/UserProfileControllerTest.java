package com.welfare.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.welfare.security.JwtAuthenticationFilter;
import com.welfare.security.JwtTokenProvider;
import com.welfare.user.api.dto.UserProfileRequest;
import com.welfare.user.api.dto.UserProfileResponse;
import com.welfare.user.application.UserProfileService;
import com.welfare.welfare.api.dto.WelfareCodeNameResponse;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserProfileControllerTest {

    private static final String EMAIL = "user@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserProfileService userProfileService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @DisplayName("내 프로필을 조회한다.")
    @Test
    void getMyProfile() throws Exception {
        // given
        when(userProfileService.get(EMAIL)).thenReturn(profileResponse());

        // when, then
        mockMvc.perform(get("/api/users/me/profile")
                        .with(authenticatedUser())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value("user-1"))
                .andExpect(jsonPath("$.data.birthDate").value("1998-05-12"))
                .andExpect(jsonPath("$.data.age").value(28))
                .andExpect(jsonPath("$.data.gender").value("F"))
                .andExpect(jsonPath("$.data.region").value("서울특별시 강남구"))
                .andExpect(jsonPath("$.data.incomeLevel").value("차상위"))
                .andExpect(jsonPath("$.data.lifeStages[0].code").value("05"))
                .andExpect(jsonPath("$.data.lifeStages[0].name").value("청년"))
                .andExpect(jsonPath("$.data.householdTypes[0].code").value("04"))
                .andExpect(jsonPath("$.data.householdTypes[0].name").value("한부모"))
                .andExpect(jsonPath("$.data.interests[0].code").value("05"))
                .andExpect(jsonPath("$.data.interests[0].name").value("주거"));

        verify(userProfileService).get(EMAIL);
    }

    @DisplayName("내 프로필을 저장한다.")
    @Test
    void saveMyProfile() throws Exception {
        // given
        UserProfileRequest request = validRequest();
        when(userProfileService.save(eq(EMAIL), any(UserProfileRequest.class))).thenReturn(profileResponse());

        // when, then
        mockMvc.perform(put("/api/users/me/profile")
                        .with(authenticatedUser())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("프로필이 저장되었습니다."))
                .andExpect(jsonPath("$.data.userId").value("user-1"))
                .andExpect(jsonPath("$.data.birthDate").value("1998-05-12"))
                .andExpect(jsonPath("$.data.region").value("서울특별시 강남구"))
                .andExpect(jsonPath("$.data.gender").value("F"))
                .andExpect(jsonPath("$.data.incomeLevel").value("차상위"));

        verify(userProfileService).save(eq(EMAIL), any(UserProfileRequest.class));
    }

    @DisplayName("프로필 저장 시 생년월일은 필수값이다.")
    @Test
    void saveProfileWithoutBirthDate() throws Exception {
        // given
        UserProfileRequest request = validRequest();
        request.setBirthDate(null);

        // when, then
        expectInvalidProfile(request, "birthDate: 생년월일은 필수입니다.");
    }

    @DisplayName("프로필 저장 시 생년월일은 오늘 이전이어야 한다.")
    @Test
    void saveProfileWithFutureBirthDate() throws Exception {
        // given
        UserProfileRequest request = validRequest();
        request.setBirthDate(LocalDate.of(2999, 1, 1));

        // when, then
        expectInvalidProfile(request, "birthDate: 생년월일은 오늘 이전이어야 합니다.");
    }

    @DisplayName("프로필 저장 시 거주 지역은 필수값이다.")
    @Test
    void saveProfileWithoutRegion() throws Exception {
        // given
        UserProfileRequest request = validRequest();
        request.setRegion(null);

        // when, then
        expectInvalidProfile(request, "region: 거주 지역은 필수입니다.");
    }

    @DisplayName("프로필 저장 시 거주 지역은 100자 이하여야 한다.")
    @Test
    void saveProfileWithTooLongRegion() throws Exception {
        // given
        UserProfileRequest request = validRequest();
        request.setRegion("가".repeat(101));

        // when, then
        expectInvalidProfile(request, "region: 거주 지역은 100자 이하여야 합니다.");
    }

    @DisplayName("프로필 저장 시 성별은 필수값이다.")
    @Test
    void saveProfileWithoutGender() throws Exception {
        // given
        UserProfileRequest request = validRequest();
        request.setGender(null);

        // when, then
        expectInvalidProfile(request, "gender: 성별은 필수입니다.");
    }

    @DisplayName("프로필 저장 시 성별은 F 또는 M 이어야 한다.")
    @Test
    void saveProfileWithWrongGender() throws Exception {
        // given
        UserProfileRequest request = validRequest();
        request.setGender("X");

        // when, then
        expectInvalidProfile(request, "gender: 성별은 F 또는 M 이어야 합니다.");
    }

    @DisplayName("프로필 저장 시 소득 수준은 30자 이하여야 한다.")
    @Test
    void saveProfileWithTooLongIncomeLevel() throws Exception {
        // given
        UserProfileRequest request = validRequest();
        request.setIncomeLevel("가".repeat(31));

        // when, then
        expectInvalidProfile(request, "incomeLevel: 소득 수준은 30자 이하여야 합니다.");
    }

    private void expectInvalidProfile(UserProfileRequest request, String message) throws Exception {
        mockMvc.perform(put("/api/users/me/profile")
                        .with(authenticatedUser())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));

        verifyNoInteractions(userProfileService);
    }

    private RequestPostProcessor authenticatedUser() {
        return request -> {
            UserDetails userDetails = User.withUsername(EMAIL)
                    .password("password")
                    .roles("USER")
                    .build();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return request;
        };
    }

    private UserProfileRequest validRequest() {
        UserProfileRequest request = new UserProfileRequest();
        request.setBirthDate(LocalDate.of(1998, 5, 12));
        request.setRegion("서울특별시 강남구");
        request.setGender("F");
        request.setIncomeLevel("차상위");
        request.setInterestCodes(List.of("04", "05"));
        request.setHouseholdTypeCodes(List.of("04"));
        return request;
    }

    private UserProfileResponse profileResponse() {
        return UserProfileResponse.builder()
                .userId("user-1")
                .birthDate(LocalDate.of(1998, 5, 12))
                .age(28)
                .gender("F")
                .region("서울특별시 강남구")
                .incomeLevel("차상위")
                .lifeStages(List.of(WelfareCodeNameResponse.builder().code("05").name("청년").build()))
                .householdTypes(List.of(WelfareCodeNameResponse.builder().code("04").name("한부모").build()))
                .interests(List.of(WelfareCodeNameResponse.builder().code("05").name("주거").build()))
                .build();
    }
}
