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
@Table(name = "welfare_form")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WelfareForm extends UuidIdentifiable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serv_id", nullable = false)
    private WelfareService welfareService;

    @Column(name = "form_name", length = 200)
    private String formName;

    @Column(name = "form_url", columnDefinition = "text")
    private String formUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Builder
    private WelfareForm(String formName, String formUrl, Integer sortOrder) {
        this.formName = formName;
        this.formUrl = formUrl;
        this.sortOrder = sortOrder == null ? 0 : sortOrder;
    }

    void setWelfareService(WelfareService welfareService) {
        this.welfareService = welfareService;
    }
}
