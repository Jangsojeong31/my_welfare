package com.welfare.welfare.application;

import com.welfare.common.exception.BusinessException;
import com.welfare.common.exception.ErrorCode;
import com.welfare.welfare.api.dto.WelfareDetailResponse;
import com.welfare.welfare.api.dto.WelfareListResponse;
import com.welfare.welfare.domain.WelfareService;
import com.welfare.welfare.infrastructure.WelfareServiceRepository;
import com.welfare.welfare.infrastructure.WelfareServiceSpecifications;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WelfareQueryService {

    private final WelfareServiceRepository welfareServiceRepository;

    public WelfareListResponse search(
            List<String> lifeStages,
            List<String> householdTypes,
            List<String> interests,
            Pageable pageable
    ) {
        Page<WelfareService> page = welfareServiceRepository.findAll(
                WelfareServiceSpecifications.withFilters(
                        normalize(lifeStages),
                        normalize(householdTypes),
                        normalize(interests)
                ),
                pageable
        );
        return WelfareListResponse.from(page);
    }

    public WelfareDetailResponse getDetail(String id) {
        WelfareService service = welfareServiceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.WELFARE_SERVICE_NOT_FOUND));
        return WelfareDetailResponse.from(service);
    }

    private List<String> normalize(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .filter(Objects::nonNull)
                .flatMap(value -> Arrays.stream(value.split(",")))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .distinct()
                .toList();
    }
}
