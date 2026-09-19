package com.welfare.ai.infrastructure;

import com.welfare.ai.domain.WelfareAiContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WelfareAiContentRepository extends JpaRepository<WelfareAiContent, String> {
}
