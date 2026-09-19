package com.welfare.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "요청 값이 올바르지 않습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "이메일 또는 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근 권한이 없습니다."),
    WELFARE_API_NOT_FOUND(HttpStatus.NOT_FOUND, "WELFARE_API_NOT_FOUND", "복지 Open API 정보를 찾을 수 없습니다."),
    WELFARE_SERVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "WELFARE_SERVICE_NOT_FOUND", "복지 서비스를 찾을 수 없습니다."),
    OPEN_API_CALL_FAILED(HttpStatus.BAD_GATEWAY, "OPEN_API_CALL_FAILED", "Open API 호출에 실패했습니다."),
    OPENAI_CALL_FAILED(HttpStatus.BAD_GATEWAY, "OPENAI_CALL_FAILED", "OpenAI 호출에 실패했습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
