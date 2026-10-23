package com.welfare.user.application;

import com.welfare.welfare.domain.Interest;
import com.welfare.welfare.domain.LifeStage;
import com.welfare.welfare.domain.WelfareService;
import com.welfare.user.application.PersonalizedWelfareMatcher.MatchResult;
import com.welfare.user.application.PersonalizedWelfareMatcher.ProfileSnapshot;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonalizedWelfareMatcherTest {

    @DisplayName("맞는 조건이 없으면 0점이다.")
    @Test
    void scoreIsZeroWhenNothingMatches() {
        WelfareService service = service("부산광역시", "해운대구");
        ProfileSnapshot profile = profile("서울특별시 강남구", "05", "청년", Set.of(), Set.of());

        MatchResult result = PersonalizedWelfareMatcher.score(service, profile);

        assertEquals(0, result.score());
        assertEquals(List.of(), result.reasons());
    }

    @DisplayName("생애주기가 같으면 점수와 이유가 생긴다.")
    @Test
    void addsScoreAndReasonWhenLifeStageMatches() {
        WelfareService service = service("부산광역시", "해운대구");
        service.replaceLifeStages(Set.of(lifeStage("05", "청년")));
        ProfileSnapshot profile = profile("서울특별시 강남구", "05", "청년", Set.of(), Set.of());

        MatchResult result = PersonalizedWelfareMatcher.score(service, profile);

        assertEquals(PersonalizedWelfareMatcher.LIFE_STAGE_MATCH, result.score());
        assertEquals(List.of("청년"), result.reasons());
    }

    @DisplayName("관심분야는 하나만 맞아도 점수가 생기고, 더 겹치면 점수가 커진다.")
    @Test
    void addsScoreAsInterestsOverlap() {
        WelfareService oneMatch = service("부산광역시", "해운대구");
        oneMatch.replaceInterests(Set.of(interest("05", "주거")));
        ProfileSnapshot profile = profile(
                "서울특별시 강남구",
                "05",
                "청년",
                Set.of("05", "03"),
                Set.of()
        );

        MatchResult single = PersonalizedWelfareMatcher.score(oneMatch, profile);

        WelfareService twoMatches = service("부산광역시", "해운대구");
        twoMatches.replaceInterests(Set.of(interest("05", "주거"), interest("03", "일자리")));

        MatchResult overlapped = PersonalizedWelfareMatcher.score(twoMatches, profile);

        assertEquals(PersonalizedWelfareMatcher.INTEREST_FIRST, single.score());
        assertEquals(List.of("주거"), single.reasons());
        assertEquals(
                PersonalizedWelfareMatcher.INTEREST_FIRST + PersonalizedWelfareMatcher.INTEREST_EXTRA,
                overlapped.score()
        );
        assertEquals(2, overlapped.reasons().size());
        assertTrue(overlapped.reasons().containsAll(List.of("주거", "일자리")));
        assertTrue(overlapped.score() > single.score());
    }

    @DisplayName("시군구가 같으면 전국 서비스보다 점수가 높다.")
    @Test
    void localRegionScoresHigherThanNationwide() {
        ProfileSnapshot profile = profile("서울특별시 강남구", "05", "청년", Set.of(), Set.of());

        MatchResult local = PersonalizedWelfareMatcher.score(service("서울특별시", "강남구"), profile);
        MatchResult nationwide = PersonalizedWelfareMatcher.score(service(null, null), profile);

        assertEquals(PersonalizedWelfareMatcher.REGION_SGG, local.score());
        assertEquals(List.of("서울특별시 강남구"), local.reasons());
        assertEquals(PersonalizedWelfareMatcher.REGION_NATIONWIDE, nationwide.score());
        assertEquals(List.of(), nationwide.reasons());
        assertTrue(local.score() > nationwide.score());
    }

    private ProfileSnapshot profile(
            String region,
            String lifeStageCode,
            String lifeStageName,
            Set<String> interestCodes,
            Set<String> householdTypeCodes
    ) {
        return new ProfileSnapshot(
                region,
                lifeStageCode,
                lifeStageName,
                interestCodes,
                householdTypeCodes,
                null,
                "M"
        );
    }

    private WelfareService service(String ctpvNm, String sggNm) {
        return WelfareService.builder()
                .servNm("복지 서비스")
                .ctpvNm(ctpvNm)
                .sggNm(sggNm)
                .build();
    }

    private LifeStage lifeStage(String code, String name) {
        LifeStage lifeStage = instantiate(LifeStage.class);
        ReflectionTestUtils.setField(lifeStage, "code", code);
        ReflectionTestUtils.setField(lifeStage, "name", name);
        return lifeStage;
    }

    private Interest interest(String code, String name) {
        Interest interest = instantiate(Interest.class);
        ReflectionTestUtils.setField(interest, "code", code);
        ReflectionTestUtils.setField(interest, "name", name);
        return interest;
    }

    private <T> T instantiate(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
