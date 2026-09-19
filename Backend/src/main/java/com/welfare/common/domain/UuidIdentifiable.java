package com.welfare.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.util.UUID;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class UuidIdentifiable {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @PrePersist
    protected void assignId() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
    }

    protected void setId(String id) {
        this.id = id;
    }
}
