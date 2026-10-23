package com.welfare.user.domain;

import com.welfare.common.exception.BusinessException;
import com.welfare.common.exception.ErrorCode;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LifeStageAgeMapperTest {

    @DisplayName("만 나이 경계에서 생애주기 코드가 바뀐다.")
    @ParameterizedTest(name = "{0}세는 {1}")
    @CsvSource({
            "5, 02",
            "6, 03",
            "12, 03",
            "13, 04",
            "18, 04",
            "19, 05",
            "34, 05",
            "35, 06",
            "64, 06",
            "65, 07"
    })
    void mapsBoundaryAgeToCode(int age, String code) {
        assertEquals(code, LifeStageAgeMapper.toCode(age));
    }

    @DisplayName("미래 생년월일은 거절한다.")
    @Test
    void rejectsFutureBirthDate() {
        LocalDate today = LocalDate.of(2026, 10, 22);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> LifeStageAgeMapper.ageOf(today.plusDays(1), today)
        );

        assertEquals(ErrorCode.INVALID_INPUT, exception.getErrorCode());
        assertEquals("생년월일은 오늘 이전이어야 합니다.", exception.getMessage());
    }
}
