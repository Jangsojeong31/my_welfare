package com.welfare.ai.application;

import com.welfare.ai.infrastructure.WelfareAiContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WelfareAiContentService {

    private final WelfareAiContentRepository welfareAiContentRepository;
}
