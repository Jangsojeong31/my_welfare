package com.welfare.welfare.infrastructure;

import com.welfare.welfare.domain.WelfareService;
import com.welfare.welfare.infrastructure.projection.WelfareListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface WelfareServiceRepositoryCustom {

    /** 목록에 필요한 컬럼만 조회한다. */
    Page<WelfareListProjection> findList(Specification<WelfareService> specification, Pageable pageable);
}
