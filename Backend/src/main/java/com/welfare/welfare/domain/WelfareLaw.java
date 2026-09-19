package com.welfare.welfare.domain;

import com.welfare.common.domain.UuidIdentifiable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "welfare_law")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WelfareLaw extends UuidIdentifiable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serv_id", nullable = false)
    private WelfareService welfareService;

    @Column(name = "law_name", length = 300)
    private String lawName;

    @Column(name = "law_url", columnDefinition = "text")
    private String lawUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Builder
    private WelfareLaw(String lawName, String lawUrl, Integer sortOrder) {
        this.lawName = lawName;
        this.lawUrl = lawUrl;
        this.sortOrder = sortOrder == null ? 0 : sortOrder;
    }

    void setWelfareService(WelfareService welfareService) {
        this.welfareService = welfareService;
    }
}
