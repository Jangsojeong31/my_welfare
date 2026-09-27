package com.welfare.user.domain;

import com.welfare.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile extends BaseTimeEntity {

    @Id
    @Column(name = "user_id", length = 50)
    private String userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "region_code", length = 100)
    private String regionCode;

    @Column(name = "income_level", length = 30)
    private String incomeLevel;

    @Column(name = "disabled_yn", nullable = false, length = 1, columnDefinition = "char(1)")
    private String disabledYn = "N";

    @Column(name = "marital_status", length = 20)
    private String maritalStatus;

    public UserProfile(User user) {
        this.user = user;
        this.disabledYn = "N";
    }

    public void update(LocalDate birthDate, String gender, String region, String incomeLevel) {
        this.birthDate = birthDate;
        this.gender = gender;
        this.regionCode = region;
        this.incomeLevel = incomeLevel;
    }
}
