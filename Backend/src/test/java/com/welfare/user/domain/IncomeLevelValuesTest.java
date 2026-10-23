package com.welfare.user.domain;

import com.welfare.common.exception.BusinessException;
import com.welfare.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IncomeLevelValuesTest {

    @DisplayName("허용된 소득 수준은 그대로 반환한다.")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "기초생활수급",
            "차상위",
            "중위소득 50% 이하",
            "중위소득 80% 이하",
            "중위소득 100% 이하",
            "중위소득 150% 이하",
            "해당 없음"
    })
    void acceptsAllowedIncome(String income) {
        assertEquals(income, IncomeLevelValues.normalize(income));
    }

    @DisplayName("비어 있는 소득 수준은 저장하지 않는다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void blankIncomeBecomesNull(String income) {
        assertNull(IncomeLevelValues.normalize(income));
    }

    @DisplayName("목록에 없는 소득 수준은 거절한다.")
    @Test
    void rejectsUnknownIncome() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> IncomeLevelValues.normalize("고소득")
        );

        assertEquals(ErrorCode.INVALID_INPUT, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("지원하지 않는 소득 수준입니다."));
    }

    @DisplayName("기초생활, 차상위, 중위소득 50% 이하만 저소득이다.")
    @Test
    void detectsLowIncome() {
        assertTrue(IncomeLevelValues.isLowIncome("기초생활수급"));
        assertTrue(IncomeLevelValues.isLowIncome("차상위"));
        assertTrue(IncomeLevelValues.isLowIncome("중위소득 50% 이하"));

        assertFalse(IncomeLevelValues.isLowIncome("중위소득 80% 이하"));
        assertFalse(IncomeLevelValues.isLowIncome("해당 없음"));
        assertFalse(IncomeLevelValues.isLowIncome(null));
    }
}
