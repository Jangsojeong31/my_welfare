package com.welfare.welfare.infrastructure;

import com.welfare.welfare.domain.WelfareService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WelfareServiceRepository
        extends JpaRepository<WelfareService, String>, JpaSpecificationExecutor<WelfareService> {

    boolean existsByApiCdAndServCd(String apiCd, String servCd);

    @Query("""
        SELECT w
        FROM WelfareService w
        WHERE
            LOWER(w.servNm) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR
            LOWER(w.servDgst) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<WelfareService> searchByKeyword(
            @Param("keyword") String keyword
    );
}
