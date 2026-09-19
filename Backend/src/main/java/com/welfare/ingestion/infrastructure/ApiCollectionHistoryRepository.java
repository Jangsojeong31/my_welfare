package com.welfare.ingestion.infrastructure;

import com.welfare.ingestion.domain.ApiCollectionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiCollectionHistoryRepository extends JpaRepository<ApiCollectionHistory, String> {
}
