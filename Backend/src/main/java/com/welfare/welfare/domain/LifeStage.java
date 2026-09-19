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
@Table(name = "life_stage")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LifeStage extends UuidIdentifiable {

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 500)
    private String description;
}
