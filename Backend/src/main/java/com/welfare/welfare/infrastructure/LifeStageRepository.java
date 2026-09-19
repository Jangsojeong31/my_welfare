package com.welfare.welfare.infrastructure;

import com.welfare.welfare.domain.LifeStage;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LifeStageRepository extends JpaRepository<LifeStage, String> {

    Optional<LifeStage> findByName(String name);

    Optional<LifeStage> findByCode(String code);
}
