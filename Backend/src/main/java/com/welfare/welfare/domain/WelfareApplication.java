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
@Table(name = "welfare_application")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WelfareApplication extends UuidIdentifiable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serv_id", nullable = false)
    private WelfareService welfareService;

    @Column(name = "serv_se_code", length = 50)
    private String servSeCode;

    @Column(name = "serv_se_detail_nm", length = 200)
    private String servSeDetailNm;

    @Column(name = "serv_se_detail_link", columnDefinition = "text")
    private String servSeDetailLink;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Builder
    private WelfareApplication(String servSeCode, String servSeDetailNm, String servSeDetailLink, Integer sortOrder) {
        this.servSeCode = servSeCode;
        this.servSeDetailNm = servSeDetailNm;
        this.servSeDetailLink = servSeDetailLink;
        this.sortOrder = sortOrder == null ? 0 : sortOrder;
    }

    void setWelfareService(WelfareService welfareService) {
        this.welfareService = welfareService;
    }
}
