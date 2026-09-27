package com.welfare.user.api.dto;

import com.welfare.user.domain.User;
import com.welfare.user.domain.UserProfile;
import com.welfare.welfare.api.dto.WelfareCodeNameResponse;
import com.welfare.welfare.domain.HouseholdType;
import com.welfare.welfare.domain.Interest;
import com.welfare.welfare.domain.LifeStage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/** 저장된 프로필과 계산된 만 나이·생애주기를 내려 주는 응답. */
@Getter
@Builder
@Schema(description = "회원 프로필 응답")
public class UserProfileResponse {

    @Schema(description = "회원 ID")
    private final String userId;

    @Schema(description = "생년월일")
    private final LocalDate birthDate;

    @Schema(description = "만 나이")
    private final Integer age;

    @Schema(description = "성별 (F/M)")
    private final String gender;

    @Schema(description = "거주 지역")
    private final String region;

    @Schema(description = "소득 수준")
    private final String incomeLevel;

    @Schema(description = "생년월일로 계산된 생애주기")
    private final List<WelfareCodeNameResponse> lifeStages;

    @Schema(description = "가구형태")
    private final List<WelfareCodeNameResponse> householdTypes;

    @Schema(description = "관심분야")
    private final List<WelfareCodeNameResponse> interests;

    /** User와 계산된 만 나이를 프로필 응답으로 변환한다. */
    public static UserProfileResponse from(User user, int age) {
        UserProfile profile = user.getProfile();
        return UserProfileResponse.builder()
                .userId(user.getId())
                .birthDate(profile == null ? null : profile.getBirthDate())
                .age(age)
                .gender(profile == null ? null : profile.getGender())
                .region(profile == null ? null : profile.getRegionCode())
                .incomeLevel(profile == null ? null : profile.getIncomeLevel())
                .lifeStages(user.getLifeStages().stream()
                        .sorted(Comparator.comparing(LifeStage::getCode))
                        .map(WelfareCodeNameResponse::from)
                        .toList())
                .householdTypes(user.getHouseholdTypes().stream()
                        .sorted(Comparator.comparing(HouseholdType::getCode))
                        .map(WelfareCodeNameResponse::from)
                        .toList())
                .interests(user.getInterests().stream()
                        .sorted(Comparator.comparing(Interest::getCode))
                        .map(WelfareCodeNameResponse::from)
                        .toList())
                .build();
    }
}
