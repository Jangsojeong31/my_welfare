package com.welfare.welfare.api.dto;

import com.welfare.welfare.domain.HouseholdType;
import com.welfare.welfare.domain.Interest;
import com.welfare.welfare.domain.LifeStage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "코드/이름 항목")
public class WelfareCodeNameResponse {

    @Schema(description = "코드")
    private final String code;

    @Schema(description = "이름")
    private final String name;

    public static WelfareCodeNameResponse from(LifeStage lifeStage) {
        return WelfareCodeNameResponse.builder()
                .code(lifeStage.getCode())
                .name(lifeStage.getName())
                .build();
    }

    public static WelfareCodeNameResponse from(HouseholdType householdType) {
        return WelfareCodeNameResponse.builder()
                .code(householdType.getCode())
                .name(householdType.getName())
                .build();
    }

    public static WelfareCodeNameResponse from(Interest interest) {
        return WelfareCodeNameResponse.builder()
                .code(interest.getCode())
                .name(interest.getName())
                .build();
    }
}
