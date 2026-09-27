package com.welfare.user.application;

import com.welfare.user.domain.IncomeLevelValues;
import com.welfare.welfare.domain.HouseholdType;
import com.welfare.welfare.domain.Interest;
import com.welfare.welfare.domain.LifeStage;
import com.welfare.welfare.domain.WelfareService;
import com.welfare.welfare.infrastructure.PersonalizedWelfareSpecifications;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

/**
 * 프로필과 복지 한 건을 비교해 점수와 매칭 이유를 계산한다.
 * 관심분야·가구형태는 AND가 아니라 겹치는 만큼 가산한다.
 */
final class PersonalizedWelfareMatcher {

    static final int LIFE_STAGE_MATCH = 4;
    static final int INTEREST_FIRST = 5;
    static final int INTEREST_EXTRA = 3;
    static final int HOUSEHOLD_MATCH = 4;
    static final int LOW_INCOME_TAG = 3;
    static final int INCOME_TEXT = 2;
    static final int REGION_SGG = 3;
    static final int REGION_NATIONWIDE = 1;
    static final int GENDER_HINT = 1;

    private static final List<String> FEMALE_KEYWORDS = List.of("여성", "임산부", "모자", "모성");
    private static final List<String> LOW_INCOME_KEYWORDS = List.of("기초생활", "차상위", "중위소득 50");

    private PersonalizedWelfareMatcher() {
    }

    /** 매칭에 필요한 프로필 값만 담은 불변 스냅샷. */
    record ProfileSnapshot(
            String region,
            String lifeStageCode,
            String lifeStageName,
            Set<String> interestCodes,
            Set<String> householdTypeCodes,
            String incomeLevel,
            String gender
    ) {
    }

    /** 가산점과 화면에 보여줄 매칭 이유(한글 이름) 목록. */
    record MatchResult(int score, List<String> reasons) {
    }

    /** 생애주기·관심분야·가구형태·소득·지역·성별 힌트를 합산한다. */
    static MatchResult score(WelfareService service, ProfileSnapshot profile) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        Set<String> lifeStageCodes = service.getLifeStages().stream()
                .map(LifeStage::getCode)
                .collect(Collectors.toSet());
        if (StringUtils.hasText(profile.lifeStageCode()) && lifeStageCodes.contains(profile.lifeStageCode())) {
            score += LIFE_STAGE_MATCH;
            reasons.add(profile.lifeStageName());
        }

        List<Interest> matchedInterests = service.getInterests().stream()
                .filter(item -> profile.interestCodes().contains(item.getCode()))
                .toList();
        if (!matchedInterests.isEmpty()) {
            score += INTEREST_FIRST + INTEREST_EXTRA * (matchedInterests.size() - 1);
            matchedInterests.forEach(item -> reasons.add(item.getName()));
        }

        List<HouseholdType> matchedHouseholds = service.getHouseholdTypes().stream()
                .filter(item -> profile.householdTypeCodes().contains(item.getCode()))
                .toList();
        if (!matchedHouseholds.isEmpty()) {
            score += HOUSEHOLD_MATCH * matchedHouseholds.size();
            matchedHouseholds.forEach(item -> reasons.add(item.getName()));
        }

        boolean hasLowIncomeTag = service.getHouseholdTypes().stream()
                .anyMatch(item -> "05".equals(item.getCode()));
        if (IncomeLevelValues.isLowIncome(profile.incomeLevel()) && hasLowIncomeTag) {
            score += LOW_INCOME_TAG;
            if (reasons.stream().noneMatch("저소득"::equals)) {
                reasons.add("저소득");
            }
        }
        if (IncomeLevelValues.isLowIncome(profile.incomeLevel())
                && containsAny(textBundle(service), LOW_INCOME_KEYWORDS)) {
            score += INCOME_TEXT;
        }

        if (isBlank(service.getCtpvNm()) && isBlank(service.getSggNm())) {
            score += REGION_NATIONWIDE;
        } else if (containsIgnoreCase(profile.region(), service.getSggNm())) {
            score += REGION_SGG;
            reasons.add(0, formatRegion(service));
        }

        if ("F".equals(profile.gender()) && containsAny(textBundle(service), FEMALE_KEYWORDS)) {
            score += GENDER_HINT;
        }

        return new MatchResult(score, reasons.stream().distinct().toList());
    }

    /** 키워드 매칭에 쓰는 제목·대상·선정기준 텍스트를 이어 붙인다. */
    private static String textBundle(WelfareService service) {
        return String.join(" ",
                nullToEmpty(service.getServNm()),
                nullToEmpty(service.getServDgst()),
                nullToEmpty(service.getTgtrDtlCn()),
                nullToEmpty(service.getSlctCritCn()),
                nullToEmpty(service.getSprtTrgtCn())
        );
    }

    /** 텍스트에 키워드가 하나라도 포함되면 true. */
    private static boolean containsAny(String text, List<String> keywords) {
        if (!StringUtils.hasText(text)) {
            return false;
        }
        String lower = text.toLowerCase();
        return keywords.stream().anyMatch(keyword -> lower.contains(keyword.toLowerCase()));
    }

    /** 프로필 지역 문자열에 시군구명이 들어 있으면 true. */
    private static boolean containsIgnoreCase(String region, String value) {
        return StringUtils.hasText(region) && StringUtils.hasText(value) && region.contains(value.trim());
    }

    /** null이거나 공백이면 true. */
    private static boolean isBlank(String value) {
        return !StringUtils.hasText(value);
    }

    /** null을 빈 문자열로 바꾼다. */
    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    /** 매칭 이유에 넣을 "시도명 시군구명" 문자열을 만든다. */
    private static String formatRegion(WelfareService service) {
        if (StringUtils.hasText(service.getCtpvNm()) && StringUtils.hasText(service.getSggNm())) {
            return service.getCtpvNm().trim() + " " + service.getSggNm().trim();
        }
        if (StringUtils.hasText(service.getSggNm())) {
            return service.getSggNm().trim();
        }
        return service.getCtpvNm() == null ? "" : service.getCtpvNm().trim();
    }
}
