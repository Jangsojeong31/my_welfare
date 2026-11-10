package com.welfare.welfare.infrastructure;

import com.welfare.welfare.domain.Interest;
import com.welfare.welfare.domain.LifeStage;
import com.welfare.welfare.domain.WelfareService;
import java.lang.reflect.Constructor;
import java.util.List;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WelfareServiceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WelfareServiceRepository welfareServiceRepository;

    @DisplayName("API 코드와 서비스 코드가 모두 같을 때만 이미 수집된 복지로 본다.")
    @Test
    void existsByApiCdAndServCd() {
        entityManager.persist(WelfareService.builder()
                .apiCd("NAW02")
                .servCd("W001")
                .servNm("청년 월세 지원")
                .build());
        entityManager.flush();

        assertTrue(welfareServiceRepository.existsByApiCdAndServCd("NAW02", "W001"));
        assertFalse(welfareServiceRepository.existsByApiCdAndServCd("LCW02", "W001"));
        assertFalse(welfareServiceRepository.existsByApiCdAndServCd("NAW02", "W002"));
    }

    @DisplayName("키워드는 서비스명이나 요약에서 대소문자 구분 없이 찾는다.")
    @Test
    void searchByKeyword() {
        saveService("청년 월세 지원", "청년의 주거비를 지원합니다.");
        saveService("Youth Card", "문화 바우처");
        saveService("노인 돌봄", "어르신 방문 돌봄");
        entityManager.flush();
        entityManager.clear();

        assertEquals(Set.of("청년 월세 지원"), names(welfareServiceRepository.searchByKeyword("월세")));
        assertEquals(Set.of("청년 월세 지원"), names(welfareServiceRepository.searchByKeyword("주거비")));
        assertEquals(Set.of("Youth Card"), names(welfareServiceRepository.searchByKeyword("youth")));
        assertEquals(Set.of(), names(welfareServiceRepository.searchByKeyword("장학금")));
    }

    @DisplayName("목록 필터는 같은 조건끼리는 하나만 맞으면 되고(OR 조건), 다른 조건끼리는 모두 맞아야 한다.(AND 조건)")
    @Test
    void filtersWithOrInsideAndAcrossConditions() {
        LifeStage youth = saveLifeStage("05", "청년");
        LifeStage middleAge = saveLifeStage("06", "중장년");
        Interest housing = saveInterest("05", "주거");
        Interest job = saveInterest("03", "일자리");

        WelfareService youthHousing = saveService("청년 주거", null);
        youthHousing.replaceLifeStages(Set.of(youth));
        youthHousing.replaceInterests(Set.of(housing));
        WelfareService middleHousing = saveService("중장년 주거", null);
        middleHousing.replaceLifeStages(Set.of(middleAge));
        middleHousing.replaceInterests(Set.of(housing));
        WelfareService youthJob = saveService("청년 일자리", null);
        youthJob.replaceLifeStages(Set.of(youth));
        youthJob.replaceInterests(Set.of(job));
        entityManager.flush();
        entityManager.clear();

        assertEquals(
                Set.of("청년 주거", "중장년 주거", "청년 일자리"),
                filter(List.of("05", "06"), List.of())
        );
        assertEquals(Set.of("청년 주거"), filter(List.of("05"), List.of("05")));
        assertEquals(Set.of("청년 주거", "청년 일자리"), filter(List.of("청년"), List.of()));
        assertEquals(
                Set.of("청년 주거", "중장년 주거", "청년 일자리"),
                filter(List.of(), List.of())
        );
    }

    private Set<String> filter(List<String> lifeStages, List<String> interests) {
        return names(welfareServiceRepository.findAll(
                WelfareServiceSpecifications.withFilters(lifeStages, List.of(), interests)
        ));
    }

    private Set<String> names(List<WelfareService> services) {
        return services.stream()
                .map(WelfareService::getServNm)
                .collect(Collectors.toSet());
    }

    private WelfareService saveService(String name, String digest) {
        return entityManager.persist(WelfareService.builder()
                .servNm(name)
                .servDgst(digest)
                .build());
    }

    private LifeStage saveLifeStage(String code, String name) {
        LifeStage lifeStage = instantiate(LifeStage.class);
        ReflectionTestUtils.setField(lifeStage, "code", code);
        ReflectionTestUtils.setField(lifeStage, "name", name);
        return entityManager.persist(lifeStage);
    }

    private Interest saveInterest(String code, String name) {
        Interest interest = instantiate(Interest.class);
        ReflectionTestUtils.setField(interest, "code", code);
        ReflectionTestUtils.setField(interest, "name", name);
        return entityManager.persist(interest);
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
