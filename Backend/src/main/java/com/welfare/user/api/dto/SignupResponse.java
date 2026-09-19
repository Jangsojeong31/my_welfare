package com.welfare.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "회원가입 응답")
public class SignupResponse {

    @Schema(description = "회원 ID")
    private final String userId;

    @Schema(description = "이메일")
    private final String email;

    @Schema(description = "이름")
    private final String name;
}
