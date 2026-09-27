package com.welfare.user.application;

import com.welfare.common.exception.BusinessException;
import com.welfare.common.exception.ErrorCode;
import com.welfare.user.api.dto.UserProfileRequest;
import com.welfare.user.api.dto.UserProfileResponse;
import com.welfare.user.domain.IncomeLevelValues;
import com.welfare.user.domain.LifeStageAgeMapper;
import com.welfare.user.domain.User;
import com.welfare.user.domain.UserProfile;
import com.welfare.user.infrastructure.UserRepository;
import com.welfare.welfare.domain.HouseholdType;
import com.welfare.welfare.domain.Interest;
import com.welfare.welfare.domain.LifeStage;
import com.welfare.welfare.infrastructure.HouseholdTypeRepository;
import com.welfare.welfare.infrastructure.InterestRepository;
import com.welfare.welfare.infrastructure.LifeStageRepository;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** 로그인한 회원의 프로필 조회·저장. 생년월일은 생애주기 코드로 변환해 User에 연결한다. */
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final LifeStageRepository lifeStageRepository;
    private final HouseholdTypeRepository householdTypeRepository;
    private final InterestRepository interestRepository;

    /** 로그인한 회원의 프로필을 저장(생성/수정)한다. */
    @Transactional
    public UserProfileResponse save(String userId, UserProfileRequest request) {
        User user = findUser(userId);
        LocalDate today = LocalDate.now();
        int age = LifeStageAgeMapper.ageOf(request.getBirthDate(), today);
        String lifeStageCode = LifeStageAgeMapper.toCode(age);

        UserProfile profile = user.getOrCreateProfile();
        profile.update(
                request.getBirthDate(),
                request.getGender(),
                request.getRegion().trim(),
                IncomeLevelValues.normalize(request.getIncomeLevel())
        );

        user.replaceLifeStages(Set.of(findLifeStage(lifeStageCode)));
        user.replaceHouseholdTypes(findHouseholdTypes(request.getHouseholdTypeCodes()));
        user.replaceInterests(findInterests(request.getInterestCodes()));

        return UserProfileResponse.from(user, age);
    }

    /** 로그인한 회원의 저장된 프로필을 조회한다. */
    @Transactional(readOnly = true)
    public UserProfileResponse get(String userId) {
        User user = findUser(userId);
        UserProfile profile = user.getProfile();
        if (profile == null || profile.getBirthDate() == null) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_FOUND);
        }
        int age = LifeStageAgeMapper.ageOf(profile.getBirthDate(), LocalDate.now());
        return UserProfileResponse.from(user, age);
    }

    /** 회원 ID로 User를 찾고, 없으면 USER_NOT_FOUND. */
    private User findUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /** 생애주기 마스터에서 코드로 한 건을 찾는다. */
    private LifeStage findLifeStage(String code) {
        return lifeStageRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INVALID_INPUT,
                        "생애주기 코드를 찾을 수 없습니다: " + code
                ));
    }

    /** 요청 코드로 가구형태를 조회하고, 없는 코드가 있으면 예외를 던진다. */
    private Set<HouseholdType> findHouseholdTypes(List<String> codes) {
        List<String> uniqueCodes = distinctCodes(codes);
        if (uniqueCodes.isEmpty()) {
            return Set.of();
        }
        List<HouseholdType> found = householdTypeRepository.findByCodeIn(uniqueCodes);
        validateCodes(uniqueCodes, found.stream().map(HouseholdType::getCode).collect(Collectors.toSet()), "가구형태");
        return new LinkedHashSet<>(found);
    }

    /** 요청 코드로 관심분야를 조회하고, 없는 코드가 있으면 예외를 던진다. */
    private Set<Interest> findInterests(List<String> codes) {
        List<String> uniqueCodes = distinctCodes(codes);
        if (uniqueCodes.isEmpty()) {
            return Set.of();
        }
        List<Interest> found = interestRepository.findByCodeIn(uniqueCodes);
        validateCodes(uniqueCodes, found.stream().map(Interest::getCode).collect(Collectors.toSet()), "관심분야");
        return new LinkedHashSet<>(found);
    }

    /** 공백을 제거하고 중복 코드를 없앤다. */
    private List<String> distinctCodes(List<String> codes) {
        if (codes == null) {
            return List.of();
        }
        return codes.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .toList();
    }

    /** 요청한 코드 중 DB에 없는 값이 있으면 INVALID_INPUT. */
    private void validateCodes(List<String> requested, Set<String> found, String label) {
        List<String> missing = requested.stream()
                .filter(code -> !found.contains(code))
                .toList();
        if (!missing.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "유효하지 않은 " + label + " 코드입니다: " + String.join(", ", missing)
            );
        }
    }
}
