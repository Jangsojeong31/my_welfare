package com.welfare.welfare.infrastructure;

import com.welfare.welfare.domain.HouseholdType;
import com.welfare.welfare.domain.LifeStage;
import com.welfare.welfare.domain.WelfareService;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersonalizedWelfareSpecificationsTest {

    private static final String REGION = "서울특별시 강남구";
    private static final String LIFE_STAGE_CODE = "05";
    private static final String TODAY = "20261110";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WelfareServiceRepository welfareServiceRepository;

    @DisplayName("전국 서비스와 프로필 지역에 포함된 서비스만 조회한다.")
    @Test
    void filtersByRegion() {
        saveService("전국", null, null, null);
        saveService("서울 전체", "서울특별시", null, null);
        saveService("강남구", "서울특별시", "강남구", null);
        saveService("서초구", "서울특별시", "서초구", null);
        saveService("부산", "부산광역시", "해운대구", null);

        Set<String> names = search(Set.of(), false);

        assertEquals(Set.of("전국", "서울 전체", "강남구"), names);
    }

    @DisplayName("생애주기 태그가 없거나, 같은 생애주기나 전생애인 서비스만 조회한다.")
    @Test
    void filtersByLifeStage() {
        LifeStage youth = saveLifeStage(LIFE_STAGE_CODE, "청년");
        LifeStage allAges = saveLifeStage(PersonalizedWelfareSpecifications.LIFE_STAGE_ALL_CODE, "전생애");
        LifeStage senior = saveLifeStage("07", "노년");

        saveService("태그 없음", null, null, null);
        saveService("청년", null, null, null).replaceLifeStages(Set.of(youth));
        saveService("전생애", null, null, null).replaceLifeStages(Set.of(allAges));
        saveService("노년", null, null, null).replaceLifeStages(Set.of(senior));

        Set<String> names = search(Set.of(), false);

        assertEquals(Set.of("태그 없음", "청년", "전생애"), names);
    }

    @DisplayName("가구형태 태그가 있으면 프로필 가구형태와 하나라도 겹쳐야 조회된다.")
    @Test
    void filtersByHouseholdType() {
        HouseholdType singleParent = saveHouseholdType("04", "한부모");
        HouseholdType disabled = saveHouseholdType("03", "장애인");

        saveService("태그 없음", null, null, null);
        saveService("한부모", null, null, null).replaceHouseholdTypes(Set.of(singleParent));
        saveService("한부모·장애인", null, null, null).replaceHouseholdTypes(Set.of(singleParent, disabled));
        saveService("장애인", null, null, null).replaceHouseholdTypes(Set.of(disabled));

        assertEquals(Set.of("태그 없음", "한부모", "한부모·장애인"), search(Set.of("04"), false));
        assertEquals(Set.of("태그 없음"), search(Set.of(), false));
    }

    @DisplayName("저소득 프로필은 저소득 가구 태그 서비스도 조회한다.")
    @Test
    void lowIncomePassesLowIncomeHouseholdTag() {
        HouseholdType lowIncome = saveHouseholdType(
                PersonalizedWelfareSpecifications.HOUSEHOLD_LOW_INCOME_CODE,
                "저소득"
        );
        saveService("저소득", null, null, null).replaceHouseholdTypes(Set.of(lowIncome));

        assertEquals(Set.of("저소득"), search(Set.of(), true));
        assertEquals(Set.of(), search(Set.of(), false));
    }

    @DisplayName("종료일이 지난 서비스는 조회하지 않는다.")
    @Test
    void excludesExpiredService() {
        saveService("종료일 없음", null, null, null);
        saveService("무기한", null, null, "99991231");
        saveService("오늘 종료", null, null, TODAY);
        saveService("종료됨", null, null, "20261109");

        Set<String> names = search(Set.of(), false);

        assertEquals(Set.of("종료일 없음", "무기한", "오늘 종료"), names);
    }

    private Set<String> search(Collection<String> householdTypeCodes, boolean lowIncome) {
        entityManager.flush();
        entityManager.clear();
        return welfareServiceRepository.findAll(
                        PersonalizedWelfareSpecifications.matchingProfile(
                                REGION,
                                LIFE_STAGE_CODE,
                                householdTypeCodes,
                                lowIncome,
                                TODAY
                        )
                ).stream()
                .map(WelfareService::getServNm)
                .collect(Collectors.toSet());
    }

    private WelfareService saveService(String name, String ctpvNm, String sggNm, String enfcEndYmd) {
        WelfareService service = WelfareService.builder()
                .servNm(name)
                .ctpvNm(ctpvNm)
                .sggNm(sggNm)
                .enfcEndYmd(enfcEndYmd)
                .build();
        return entityManager.persist(service);
    }

    private LifeStage saveLifeStage(String code, String name) {
        LifeStage lifeStage = instantiate(LifeStage.class);
        ReflectionTestUtils.setField(lifeStage, "code", code);
        ReflectionTestUtils.setField(lifeStage, "name", name);
        return entityManager.persist(lifeStage);
    }

    private HouseholdType saveHouseholdType(String code, String name) {
        HouseholdType householdType = instantiate(HouseholdType.class);
        ReflectionTestUtils.setField(householdType, "code", code);
        ReflectionTestUtils.setField(householdType, "name", name);
        return entityManager.persist(householdType);
    }

    private <T> T instantiate(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
