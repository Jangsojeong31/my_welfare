package com.welfare.welfare.infrastructure;

import com.welfare.welfare.domain.HouseholdType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseholdTypeRepository extends JpaRepository<HouseholdType, String> {

    Optional<HouseholdType> findByName(String name);

    Optional<HouseholdType> findByCode(String code);

    List<HouseholdType> findByCodeIn(Collection<String> codes);
}
