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
@Table(name = "welfare_link")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WelfareLink extends UuidIdentifiable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serv_id", nullable = false)
    private WelfareService welfareService;

    @Column(name = "serv_se_code", length = 50)
    private String servSeCode;

    @Column(name = "link_name", length = 200)
    private String linkName;

    @Column(name = "link_url", columnDefinition = "text")
    private String linkUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Builder
    private WelfareLink(String servSeCode, String linkName, String linkUrl, Integer sortOrder) {
        this.servSeCode = servSeCode;
        this.linkName = linkName;
        this.linkUrl = linkUrl;
        this.sortOrder = sortOrder == null ? 0 : sortOrder;
    }

    void setWelfareService(WelfareService welfareService) {
        this.welfareService = welfareService;
    }
}
