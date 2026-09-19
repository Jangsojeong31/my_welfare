package com.welfare.ingestion.application;

import com.welfare.welfare.domain.HouseholdType;
import com.welfare.welfare.domain.Interest;
import com.welfare.welfare.domain.LifeStage;
import com.welfare.welfare.infrastructure.HouseholdTypeRepository;
import com.welfare.welfare.infrastructure.InterestRepository;
import com.welfare.welfare.infrastructure.LifeStageRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WelfareCodeMatcher {

    private final LifeStageRepository lifeStageRepository;
    private final HouseholdTypeRepository householdTypeRepository;
    private final InterestRepository interestRepository;

    @Transactional(readOnly = true)
    public Set<LifeStage> matchLifeStages(List<String> values) {
        List<LifeStage> all = lifeStageRepository.findAll();
        Set<LifeStage> matched = new HashSet<>();
        for (String value : values) {
            findByNameOrCode(all, value, LifeStage::getName, LifeStage::getCode).ifPresent(matched::add);
        }
        return matched;
    }

    @Transactional(readOnly = true)
    public Set<HouseholdType> matchHouseholdTypes(List<String> values) {
        List<HouseholdType> all = householdTypeRepository.findAll();
        Set<HouseholdType> matched = new HashSet<>();
        for (String value : values) {
            findByNameOrCode(all, value, HouseholdType::getName, HouseholdType::getCode).ifPresent(matched::add);
        }
        return matched;
    }

    @Transactional(readOnly = true)
    public Set<Interest> matchInterests(List<String> values) {
        List<Interest> all = interestRepository.findAll();
        Set<Interest> matched = new HashSet<>();
        for (String value : values) {
            findByNameOrCode(all, value, Interest::getName, Interest::getCode).ifPresent(matched::add);
        }
        return matched;
    }

    private <T> Optional<T> findByNameOrCode(
            List<T> all,
            String raw,
            java.util.function.Function<T, String> nameGetter,
            java.util.function.Function<T, String> codeGetter
    ) {
        String token = normalize(raw);
        if (token == null) {
            return Optional.empty();
        }
        Optional<T> exact = all.stream()
                .filter(item -> token.equals(normalize(nameGetter.apply(item))) || token.equals(normalize(codeGetter.apply(item))))
                .findFirst();
        if (exact.isPresent()) {
            return exact;
        }
        return all.stream()
                .filter(item -> {
                    String name = normalize(nameGetter.apply(item));
                    return name != null && (name.contains(token) || token.contains(name));
                })
                .findFirst();
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value
                .replace("·", "")
                .replace("ㆍ", "")
                .replace(" ", "")
                .replace("-", "")
                .trim()
                .toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }
}
