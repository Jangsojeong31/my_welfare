package com.welfare.user.api;

import com.welfare.common.api.dto.ApiResponse;
import com.welfare.user.api.dto.UserProfileRequest;
import com.welfare.user.api.dto.UserProfileResponse;
import com.welfare.user.application.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 내 프로필 조회(GET)·저장(PUT) API. JWT 인증이 필요하다. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/profile")
@Tag(name = "회원 프로필", description = "로그인한 회원의 프로필 조회 및 저장")
public class UserProfileController {

    private final UserProfileService userProfileService;

    /** 로그인한 회원의 저장된 프로필을 조회한다. */
    @Operation(summary = "내 프로필 조회", description = "로그인한 회원의 프로필과 계산된 생애주기를 조회합니다.")
    @GetMapping
    public ApiResponse<UserProfileResponse> get(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.ok(userProfileService.get(userDetails.getUsername()));
    }

    /** 로그인한 회원의 프로필을 저장한다. 생년월일은 생애주기 코드로 변환되어 함께 저장된다. */
    @Operation(
            summary = "내 프로필 저장",
            description = """
                    로그인한 회원의 프로필을 저장합니다.
                    생년월일로 만 나이를 계산해 생애주기(02~07)를 저장하며, 전생애(01)는 저장하지 않습니다.
                    관심분야·가구형태는 코드로 복수 선택할 수 있습니다.
                    """
    )
    @PutMapping
    public ApiResponse<UserProfileResponse> save(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserProfileRequest request
    ) {
        return ApiResponse.ok(userProfileService.save(userDetails.getUsername(), request), "프로필이 저장되었습니다.");
    }
}
