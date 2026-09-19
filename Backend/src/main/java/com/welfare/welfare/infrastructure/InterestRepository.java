package com.welfare.welfare.infrastructure;

import com.welfare.welfare.domain.Interest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, String> {

    Optional<Interest> findByName(String name);

    Optional<Interest> findByCode(String code);
}
