package com.welfare.welfare.domain;

import com.welfare.common.domain.UuidIdentifiable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "welfare_api")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WelfareApi extends UuidIdentifiable {

    @Column(name = "api_cd", length = 20)
    private String apiCd;

    @Column(name = "api_name", length = 50)
    private String apiName;

    @Column(name = "api_source_cd", length = 20)
    private String apiSourceCd;

    @Column(name = "api_url", length = 200)
    private String apiUrl;

    @Column(name = "description", length = 300)
    private String description;
}
