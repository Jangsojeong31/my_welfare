package com.welfare.user.domain;

import com.welfare.common.exception.BusinessException;
import com.welfare.common.exception.ErrorCode;
import java.util.List;
import java.util.Set;

/** 프로필 소득 수준 허용 값과 저소득 판별. */
public final class IncomeLevelValues {

    public static final List<String> ALL = List.of(
            "기초생활수급",
            "차상위",
            "중위소득 50% 이하",
            "중위소득 80% 이하",
            "중위소득 100% 이하",
            "중위소득 150% 이하",
            "해당 없음"
    );

    public static final Set<String> LOW_INCOME = Set.of(
            "기초생활수급",
            "차상위",
            "중위소득 50% 이하"
    );

    private static final Set<String> ALLOWED = Set.copyOf(ALL);

    private IncomeLevelValues() {
    }

    /** 공백이면 null, 허용 목록에 없으면 예외, 그 외는 trim 한 값을 반환한다. */
    public static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (!ALLOWED.contains(trimmed)) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "지원하지 않는 소득 수준입니다. 가능한 값: " + String.join(", ", ALL)
            );
        }
        return trimmed;
    }

    /** 기초생활·차상위·중위소득 50% 이하면 저소득으로 본다. */
    public static boolean isLowIncome(String value) {
        return value != null && LOW_INCOME.contains(value);
    }
}
