package com.welfare.user.application;

import com.welfare.common.exception.BusinessException;
import com.welfare.common.exception.ErrorCode;
import com.welfare.user.application.PersonalizedWelfareMatcher.MatchResult;
import com.welfare.user.application.PersonalizedWelfareMatcher.ProfileSnapshot;
import com.welfare.user.domain.IncomeLevelValues;
import com.welfare.user.domain.LifeStageAgeMapper;
import com.welfare.user.domain.User;
import com.welfare.user.domain.UserProfile;
import com.welfare.user.infrastructure.UserRepository;
import com.welfare.welfare.api.dto.WelfareListItemResponse;
import com.welfare.welfare.api.dto.WelfareListResponse;
import com.welfare.welfare.domain.HouseholdType;
import com.welfare.welfare.domain.Interest;
import com.welfare.welfare.domain.LifeStage;
import com.welfare.welfare.domain.WelfareService;
import com.welfare.welfare.infrastructure.PersonalizedWelfareSpecifications;
import com.welfare.welfare.infrastructure.WelfareServiceRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 저장된 프로필로 맞춤 복지 목록을 조회한다.
 * DB에서 지역·생애주기·가구형태 후보를 거른 뒤, 메모리에서 점수를 매겨 정렬한다.
 */
@Service
@RequiredArgsConstructor
public class PersonalizedWelfareService {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter YMD = DateTimeFormatter.BASIC_ISO_DATE;

    private final UserRepository userRepository;
    private final WelfareServiceRepository welfareServiceRepository;

    /** 저장된 프로필을 기준으로 맞춤 복지 목록을 점수순으로 조회한다. */
    @Transactional(readOnly = true)
    public WelfareListResponse search(String userId, Pageable pageable) {
        User user = userRepository.findDetailById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        UserProfile profile = user.getProfile();
        if (profile == null || profile.getBirthDate() == null) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_FOUND);
        }

        ProfileSnapshot snapshot = toSnapshot(user, profile);
        LocalDate today = LocalDate.now(SEOUL);
        List<WelfareService> candidates = welfareServiceRepository.findAll(
                PersonalizedWelfareSpecifications.matchingProfile(
                        snapshot.region(),
                        snapshot.lifeStageCode(),
                        snapshot.householdTypeCodes(),
                        IncomeLevelValues.isLowIncome(snapshot.incomeLevel()),
                        today.format(YMD)
                )
        );

        List<WelfareListItemResponse> ranked = candidates.stream()
                .map(service -> {
                    MatchResult match = PersonalizedWelfareMatcher.score(service, snapshot);
                    return new Ranked(service, match);
                })
                .sorted(Comparator
                        .comparingInt((Ranked item) -> item.match.score()).reversed()
                        .thenComparing(item -> item.service.getInqNum() == null ? 0 : item.service.getInqNum(),
                                Comparator.reverseOrder()))
                .map(item -> WelfareListItemResponse.from(
                        item.service(),
                        item.match().score(),
                        item.match().reasons()
                ))
                .toList();

        int page = Math.max(pageable.getPageNumber(), 0);
        int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : 20;
        int from = Math.min(page * size, ranked.size());
        int to = Math.min(from + size, ranked.size());
        return WelfareListResponse.fromItems(
                new PageImpl<>(ranked.subList(from, to), PageRequest.of(page, size), ranked.size())
        );
    }

    /** 점수 정렬용 복지와 매칭 결과 묶음. */
    private record Ranked(WelfareService service, MatchResult match) {
    }

    /** 나이·코드 집합을 매칭에 쓰기 쉬운 스냅샷으로 만든다. */
    private ProfileSnapshot toSnapshot(User user, UserProfile profile) {
        int age = LifeStageAgeMapper.ageOf(profile.getBirthDate(), LocalDate.now(SEOUL));
        String lifeStageCode = user.getLifeStages().stream()
                .map(LifeStage::getCode)
                .filter(code -> code != null && !PersonalizedWelfareSpecifications.LIFE_STAGE_ALL_CODE.equals(code))
                .findFirst()
                .orElseGet(() -> LifeStageAgeMapper.toCode(age));
        String lifeStageName = user.getLifeStages().stream()
                .filter(stage -> lifeStageCode.equals(stage.getCode()))
                .map(LifeStage::getName)
                .findFirst()
                .orElse(lifeStageCode);

        return new ProfileSnapshot(
                profile.getRegionCode() == null ? "" : profile.getRegionCode().trim(),
                lifeStageCode,
                lifeStageName,
                user.getInterests().stream().map(Interest::getCode).collect(Collectors.toSet()),
                user.getHouseholdTypes().stream().map(HouseholdType::getCode).collect(Collectors.toSet()),
                profile.getIncomeLevel(),
                profile.getGender()
        );
    }
}
