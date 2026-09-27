package com.welfare.welfare.infrastructure;

import com.welfare.welfare.domain.WelfareService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * 맞춤 조회 1단계 하드 필터.
 * 지역·생애주기·가구형태가 맞고, 만료되지 않은 복지다.
 * 가구형태는 태그가 없으면 통과, 있으면 프로필 코드와 하나라도 겹쳐야 한다.
 * 저소득(05)은 가구 코드 또는 소득 수준으로도 통과한다.
 */
public final class PersonalizedWelfareSpecifications {

    public static final String LIFE_STAGE_ALL_CODE = "01";
    public static final String HOUSEHOLD_LOW_INCOME_CODE = "05";

    private PersonalizedWelfareSpecifications() {
    }

    /** 지역·생애주기·가구형태·유효기간 조건을 AND로 묶는다. */
    public static Specification<WelfareService> matchingProfile(
            String region,
            String lifeStageCode,
            Collection<String> householdTypeCodes,
            boolean lowIncome,
            String todayYmd
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(regionMatches(root, cb, region));
            if (StringUtils.hasText(lifeStageCode)) {
                predicates.add(lifeStageEligible(root, query, cb, lifeStageCode));
            }
            predicates.add(householdEligible(root, query, cb, householdTypeCodes, lowIncome));
            predicates.add(notExpired(root, cb, todayYmd));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    /** 시도·시군구가 비어 있거나(전국), 프로필 지역에 해당 지명이 포함되면 통과. */
    private static Predicate regionMatches(Root<WelfareService> root, CriteriaBuilder cb, String region) {
        String normalizedRegion = region == null ? "" : region.trim();
        Expression<String> ctpv = root.get("ctpvNm");
        Expression<String> sgg = root.get("sggNm");
        return cb.and(
                blankOrContainedIn(cb, ctpv, normalizedRegion),
                blankOrContainedIn(cb, sgg, normalizedRegion)
        );
    }

    /** 컬럼이 공백이거나, 프로필 지역 문자열이 컬럼 값을 포함하면 통과. */
    private static Predicate blankOrContainedIn(CriteriaBuilder cb, Expression<String> column, String region) {
        Predicate blank = cb.or(cb.isNull(column), cb.equal(cb.trim(column), ""));
        if (!StringUtils.hasText(region)) {
            return blank;
        }
        Predicate contained = cb.like(cb.literal(region), cb.concat(cb.concat("%", column), "%"));
        return cb.or(blank, contained);
    }

    /** 생애주기 태그가 없거나, 사용자 코드 또는 전생애(01)가 있으면 통과. */
    private static Predicate lifeStageEligible(
            Root<WelfareService> root,
            jakarta.persistence.criteria.CriteriaQuery<?> query,
            CriteriaBuilder cb,
            String lifeStageCode
    ) {
        Subquery<String> hasAny = query.subquery(String.class);
        Root<WelfareService> anyRoot = hasAny.from(WelfareService.class);
        anyRoot.join("lifeStages");
        hasAny.select(anyRoot.get("id"))
                .where(cb.equal(anyRoot.get("id"), root.get("id")));

        Subquery<String> matching = query.subquery(String.class);
        Root<WelfareService> matchRoot = matching.from(WelfareService.class);
        Join<?, ?> lifeStage = matchRoot.join("lifeStages");
        matching.select(matchRoot.get("id"))
                .where(
                        cb.equal(matchRoot.get("id"), root.get("id")),
                        lifeStage.get("code").in(List.of(lifeStageCode, LIFE_STAGE_ALL_CODE))
                );
        return cb.or(cb.not(cb.exists(hasAny)), cb.exists(matching));
    }

    /**
     * 가구형태 태그가 없으면 통과.
     * 있으면 프로필 코드와 하나라도 겹쳐야 하고, 저소득(05)은 소득 수준으로도 인정한다.
     * 프로필에 가구·저소득이 없으면 태그가 있는 복지는 제외한다.
     */
    private static Predicate householdEligible(
            Root<WelfareService> root,
            jakarta.persistence.criteria.CriteriaQuery<?> query,
            CriteriaBuilder cb,
            Collection<String> householdTypeCodes,
            boolean lowIncome
    ) {
        Subquery<String> hasAny = query.subquery(String.class);
        Root<WelfareService> anyRoot = hasAny.from(WelfareService.class);
        anyRoot.join("householdTypes");
        hasAny.select(anyRoot.get("id"))
                .where(cb.equal(anyRoot.get("id"), root.get("id")));

        Set<String> eligibleCodes = new LinkedHashSet<>();
        if (householdTypeCodes != null) {
            householdTypeCodes.stream()
                    .filter(StringUtils::hasText)
                    .forEach(eligibleCodes::add);
        }
        if (lowIncome) {
            eligibleCodes.add(HOUSEHOLD_LOW_INCOME_CODE);
        }
        if (eligibleCodes.isEmpty()) {
            return cb.not(cb.exists(hasAny));
        }

        Subquery<String> matching = query.subquery(String.class);
        Root<WelfareService> matchRoot = matching.from(WelfareService.class);
        Join<?, ?> householdType = matchRoot.join("householdTypes");
        matching.select(matchRoot.get("id"))
                .where(
                        cb.equal(matchRoot.get("id"), root.get("id")),
                        householdType.get("code").in(eligibleCodes)
                );
        return cb.or(cb.not(cb.exists(hasAny)), cb.exists(matching));
    }

    /** 종료일이 없거나 무기한이거나 오늘 이후이면 통과. */
    private static Predicate notExpired(Root<WelfareService> root, CriteriaBuilder cb, String todayYmd) {
        Expression<String> endYmd = root.get("enfcEndYmd");
        return cb.or(
                cb.isNull(endYmd),
                cb.equal(cb.trim(endYmd), ""),
                cb.equal(endYmd, "99991231"),
                cb.greaterThanOrEqualTo(endYmd, todayYmd)
        );
    }
}
