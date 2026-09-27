package com.welfare.user.domain;

import com.welfare.common.exception.BusinessException;
import com.welfare.common.exception.ErrorCode;
import java.time.LocalDate;
import java.time.Period;

/** 생년월일로 나이를 계산해 생애주기 코드(02~07)를 반환한다. 전생애(01)는 사용하지 않는다. */
public final class LifeStageAgeMapper {

    private LifeStageAgeMapper() {
    }

    /** 생년월일 기준 만 나이를 계산한다. */
    public static int ageOf(LocalDate birthDate, LocalDate today) {
        if (birthDate == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "생년월일은 필수입니다.");
        }
        if (birthDate.isAfter(today)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "생년월일은 오늘 이전이어야 합니다.");
        }
        return Period.between(birthDate, today).getYears();
    }

    /** 만 나이를 복지로 생애주기 코드(영유아 02 ~ 노년 07)로 변환한다. */
    public static String toCode(int age) {
        if (age < 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "나이가 올바르지 않습니다.");
        }
        if (age <= 5) {
            return "02";
        }
        if (age <= 12) {
            return "03";
        }
        if (age <= 18) {
            return "04";
        }
        if (age <= 34) {
            return "05";
        }
        if (age <= 64) {
            return "06";
        }
        return "07";
    }
}
