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
@Table(name = "welfare_contact")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WelfareContact extends UuidIdentifiable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serv_id", nullable = false)
    private WelfareService welfareService;

    @Column(name = "serv_se_code", length = 50)
    private String servSeCode;

    @Column(name = "contact_name", length = 200)
    private String contactName;

    @Column(name = "contact_value", length = 500)
    private String contactValue;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Builder
    private WelfareContact(String servSeCode, String contactName, String contactValue, Integer sortOrder) {
        this.servSeCode = servSeCode;
        this.contactName = contactName;
        this.contactValue = contactValue;
        this.sortOrder = sortOrder == null ? 0 : sortOrder;
    }

    void setWelfareService(WelfareService welfareService) {
        this.welfareService = welfareService;
    }
}
