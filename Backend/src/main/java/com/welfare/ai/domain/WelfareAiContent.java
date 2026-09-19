package com.welfare.ai.domain;

import com.welfare.common.domain.UuidTimeEntity;
import com.welfare.welfare.domain.WelfareService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "welfare_ai_content")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WelfareAiContent extends UuidTimeEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serv_id", nullable = false)
    private WelfareService welfareService;

    @Column(name = "summary", columnDefinition = "text")
    private String summary;

    @Column(name = "application_summary", columnDefinition = "text")
    private String applicationSummary;

    @Column(name = "target_summary", columnDefinition = "text")
    private String targetSummary;

    @Column(name = "ai_model", length = 100)
    private String aiModel;

    @Column(name = "prompt_version", length = 50)
    private String promptVersion;
}
