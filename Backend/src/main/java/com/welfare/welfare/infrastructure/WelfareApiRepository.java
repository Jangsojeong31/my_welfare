package com.welfare.welfare.infrastructure;

import com.welfare.welfare.domain.WelfareApi;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WelfareApiRepository extends JpaRepository<WelfareApi, String> {

    Optional<WelfareApi> findByApiCd(String apiCd);
}
