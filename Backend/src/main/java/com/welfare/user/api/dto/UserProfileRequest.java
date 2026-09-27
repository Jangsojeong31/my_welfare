package com.welfare.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 프로필 저장 요청. 관심분야·가구형태는 코드 배열로 받는다. */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회원 프로필 저장 요청")
public class UserProfileRequest {

    @Schema(description = "생년월일", example = "1998-05-12")
    @NotNull(message = "생년월일은 필수입니다.")
    @PastOrPresent(message = "생년월일은 오늘 이전이어야 합니다.")
    private LocalDate birthDate;

    @Schema(description = "거주 지역 (시도/시군구)", example = "서울특별시 강남구")
    @NotBlank(message = "거주 지역은 필수입니다.")
    @Size(max = 100, message = "거주 지역은 100자 이하여야 합니다.")
    private String region;

    @Schema(description = "관심분야 코드 목록 (중복 선택 가능)", example = "[\"04\", \"05\"]")
    private List<String> interestCodes = new ArrayList<>();

    @Schema(description = "가구형태 코드 목록 (중복 선택 가능)", example = "[\"04\", \"05\"]")
    private List<String> householdTypeCodes = new ArrayList<>();

    @Schema(
            description = "소득 수준",
            example = "차상위",
            allowableValues = {
                    "기초생활수급",
                    "차상위",
                    "중위소득 50% 이하",
                    "중위소득 80% 이하",
                    "중위소득 100% 이하",
                    "중위소득 150% 이하",
                    "해당 없음"
            }
    )
    @Size(max = 30, message = "소득 수준은 30자 이하여야 합니다.")
    private String incomeLevel;

    @Schema(description = "성별 (F/M)", example = "F")
    @NotBlank(message = "성별은 필수입니다.")
    @Pattern(regexp = "F|M", message = "성별은 F 또는 M 이어야 합니다.")
    private String gender;
}
