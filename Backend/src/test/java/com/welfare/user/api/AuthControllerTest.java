package com.welfare.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.welfare.security.JwtAuthenticationFilter;
import com.welfare.security.JwtTokenProvider;
import com.welfare.user.api.dto.SignupRequest;
import com.welfare.user.api.dto.SignupResponse;
import com.welfare.user.application.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @DisplayName("이름, 이메일, 비밀번호를 입력해서 신규 회원가입을 한다.")
    @Test
    void newSignup() throws Exception {
        // given
        SignupRequest signupRequest = SignupRequest.builder()
                .name("name")
                .email("test@example.com")
                .password("password")
                .build();

        SignupResponse signupResponse = SignupResponse.builder()
                        .userId("회원 ID")
                        .email("테스트이메일@example.com")
                        .name("이름")
                        .build();

        when(authService.signup(any())).thenReturn(signupResponse);

        // when, then
        mockMvc.perform(post("/api/auth/signup")
                        .content(objectMapper.writeValueAsString(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
                .andExpect(jsonPath("$.data.userId").value("회원 ID"));

    }

    @DisplayName("회원가입 시 이름은 필수값이다.")
    @Test
    void signupWithoutName() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();

        // when, then
        mockMvc.perform(post("/api/auth/signup")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("name: 이름은 필수입니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));
    }

    @DisplayName("회원가입 시 이메일은 필수값이다.")
    @Test
    void signupWithoutEmail() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .name("name")
                .password("password")
                .build();

        // when, then
        mockMvc.perform(post("/api/auth/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("email: 이메일은 필수입니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));
    }

    @DisplayName("회원가입 시 입력한 이메일은 올바른 이메일 형식이어야 한다.")
    @Test
    void signupWithWrongEmail() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .name("name")
                .email("잘못된 이메일 형식")
                .password("password")
                .build();

        // when, then
        mockMvc.perform(post("/api/auth/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("email: 이메일 형식이 올바르지 않습니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));
    }

    @DisplayName("회원가입 시 비밀번호는 필수값이다.")
    @Test
    void signupWithoutPassword() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .name("name")
                .email("test@example.com")
                .build();

        // when, then
        mockMvc.perform(post("/api/auth/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("password: 비밀번호는 필수입니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));
    }

    @DisplayName("회원가입 시 입력한 비밀번호는 8자 이상이어야 한다.")
    @Test
    void signupWithWrongPassword() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .name("name")
                .email("test@example.com")
                .password("aa")
                .build();

        // when, then
        mockMvc.perform(post("/api/auth/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("password: 비밀번호는 8자 이상이어야 합니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));
    }


}