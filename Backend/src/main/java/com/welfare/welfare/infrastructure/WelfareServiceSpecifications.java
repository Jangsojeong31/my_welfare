package com.welfare.welfare.infrastructure;

import com.welfare.welfare.domain.WelfareService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class WelfareServiceSpecifications {

    private WelfareServiceSpecifications() {
    }

    public static Specification<WelfareService> withFilters(
            List<String> lifeStages,
            List<String> householdTypes,
            List<String> interests
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (hasValues(lifeStages)) {
                predicates.add(existsByCodeOrName(root, query, cb, "lifeStages", lifeStages));
            }
            if (hasValues(householdTypes)) {
                predicates.add(existsByCodeOrName(root, query, cb, "householdTypes", householdTypes));
            }
            if (hasValues(interests)) {
                predicates.add(existsByCodeOrName(root, query, cb, "interests", interests));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static Predicate existsByCodeOrName(
            Root<WelfareService> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            String associationName,
            Collection<String> values
    ) {
        Subquery<String> subquery = query.subquery(String.class);
        Root<WelfareService> subRoot = subquery.from(WelfareService.class);
        Join<?, ?> join = subRoot.join(associationName);
        subquery.select(subRoot.get("id"))
                .where(
                        cb.equal(subRoot.get("id"), root.get("id")),
                        cb.or(join.get("code").in(values), join.get("name").in(values))
                );
        return cb.exists(subquery);
    }

    private static boolean hasValues(List<String> values) {
        return values != null && !values.isEmpty();
    }
}
