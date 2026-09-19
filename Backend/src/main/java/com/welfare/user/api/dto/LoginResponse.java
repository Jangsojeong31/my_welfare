package com.welfare.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "로그인 응답")
public class LoginResponse {

    @Schema(description = "JWT Access Token")
    private final String accessToken;

    @Schema(description = "토큰 타입", example = "Bearer")
    private final String tokenType;

    @Schema(description = "만료 시간(ms)")
    private final long expiresIn;

    @Schema(description = "회원 ID")
    private final String userId;

    @Schema(description = "이메일")
    private final String email;

    @Schema(description = "이름")
    private final String name;
}
