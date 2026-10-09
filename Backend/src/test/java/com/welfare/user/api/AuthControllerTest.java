package com.welfare.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.welfare.security.JwtAuthenticationFilter;
import com.welfare.security.JwtTokenProvider;
import com.welfare.user.api.dto.EmailCheckResponse;
import com.welfare.user.api.dto.LoginRequest;
import com.welfare.user.api.dto.LoginResponse;
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
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @DisplayName("이메일과 비밀번호로 로그인한다.")
    @Test
    void login() throws Exception {
        // given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        LoginResponse response = LoginResponse.builder()
                .accessToken("access-token")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .userId("user-1")
                .email("test@example.com")
                .name("홍길동")
                .build();

        when(authService.login(any())).thenReturn(response);

        // when, then
        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(86400000))
                .andExpect(jsonPath("$.data.userId").value("user-1"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.name").value("홍길동"));
    }

    @DisplayName("로그인 시 이메일은 필수값이다.")
    @Test
    void loginWithoutEmail() throws Exception {
        // given
        LoginRequest request = new LoginRequest();
        request.setPassword("password");

        // when, then
        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("email: 이메일은 필수입니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));
    }

    @DisplayName("로그인 시 입력한 이메일은 올바른 이메일 형식이어야 한다.")
    @Test
    void loginWithWrongEmail() throws Exception {
        // given
        LoginRequest request = new LoginRequest();
        request.setEmail("잘못된 이메일 형식");
        request.setPassword("password");

        // when, then
        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("email: 이메일 형식이 올바르지 않습니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));
    }

    @DisplayName("로그인 시 비밀번호는 필수값이다.")
    @Test
    void loginWithoutPassword() throws Exception {
        // given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");

        // when, then
        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("password: 비밀번호는 필수입니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));
    }

    @DisplayName("사용 가능한 이메일인지 확인한다.")
    @Test
    void checkEmailAvailable() throws Exception {
        // given
        EmailCheckResponse response = EmailCheckResponse.builder()
                .email("test@example.com")
                .duplicated(false)
                .build();

        when(authService.checkEmail("test@example.com")).thenReturn(response);

        // when, then
        mockMvc.perform(get("/api/auth/email-check")
                        .param("email", "test@example.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.duplicated").value(false));

        verify(authService).checkEmail("test@example.com");
    }

    @DisplayName("이미 사용 중인 이메일은 중복으로 응답한다.")
    @Test
    void checkEmailDuplicated() throws Exception {
        // given
        EmailCheckResponse response = EmailCheckResponse.builder()
                .email("test@example.com")
                .duplicated(true)
                .build();

        when(authService.checkEmail("test@example.com")).thenReturn(response);

        // when, then
        mockMvc.perform(get("/api/auth/email-check")
                        .param("email", "test@example.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.duplicated").value(true));
    }

    @DisplayName("이메일 중복 확인 시 이메일 파라미터는 필수값이다.")
    @Test
    void checkEmailWithoutEmail() throws Exception {
        // when, then
        mockMvc.perform(get("/api/auth/email-check")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(
                        "Required request parameter 'email' for method parameter type String is not present"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));
    }

    @DisplayName("이메일 중복 확인 시 이메일은 비어 있을 수 없다.")
    @Test
    void checkEmailWithBlankEmail() throws Exception {
        // when, then
        mockMvc.perform(get("/api/auth/email-check")
                        .param("email", " ")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.containsString("checkEmail.email: 이메일은 필수입니다.")))
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.containsString("checkEmail.email: 이메일 형식이 올바르지 않습니다.")))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));
    }

    @DisplayName("이메일 중복 확인 시 입력한 이메일은 올바른 이메일 형식이어야 한다.")
    @Test
    void checkEmailWithWrongEmail() throws Exception {
        // when, then
        mockMvc.perform(get("/api/auth/email-check")
                        .param("email", "잘못된 이메일 형식")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("checkEmail.email: 이메일 형식이 올바르지 않습니다."))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));
    }

}