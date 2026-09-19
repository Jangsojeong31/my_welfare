package com.welfare.user.api;

import com.welfare.common.api.dto.ApiResponse;
import com.welfare.user.api.dto.EmailCheckResponse;
import com.welfare.user.api.dto.LoginRequest;
import com.welfare.user.api.dto.LoginResponse;
import com.welfare.user.api.dto.SignupRequest;
import com.welfare.user.api.dto.SignupResponse;
import com.welfare.user.application.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "인증", description = "회원가입, 로그인, 이메일 중복 확인")
public class AuthController {

    private final AuthService authService;

    /** 이메일, 비밀번호, 이름으로 신규 회원을 등록한다. */
    @SecurityRequirements
    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름으로 신규 회원을 등록합니다.")
    @PostMapping("/signup")
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.ok(authService.signup(request), "회원가입이 완료되었습니다.");
    }

    /** 이메일과 비밀번호로 로그인하고 JWT를 발급한다. */
    @SecurityRequirements
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 JWT Access Token을 발급합니다.")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    /** 회원가입 전 이메일 중복 여부를 확인한다. */
    @SecurityRequirements
    @Operation(summary = "이메일 중복 확인", description = "회원가입 전 이메일 사용 가능 여부를 확인합니다.")
    @GetMapping("/email-check")
    public ApiResponse<EmailCheckResponse> checkEmail(
            @Parameter(description = "중복 확인할 이메일", example = "user@example.com")
            @RequestParam
            @NotBlank(message = "이메일은 필수입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            String email
    ) {
        return ApiResponse.ok(authService.checkEmail(email));
    }
}
