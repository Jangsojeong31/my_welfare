package com.welfare.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "이메일 중복 확인 응답")
public class EmailCheckResponse {

    @Schema(description = "확인할 이메일")
    private final String email;

    @Schema(description = "중복 여부 (true: 이미 사용 중)")
    private final boolean duplicated;
}
