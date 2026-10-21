package com.welfare.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.welfare.user.domain.LifeStageAgeMapper;
import com.welfare.welfare.domain.HouseholdType;
import com.welfare.welfare.domain.Interest;
import com.welfare.welfare.domain.LifeStage;
import com.welfare.welfare.domain.WelfareService;
import com.welfare.welfare.infrastructure.HouseholdTypeRepository;
import com.welfare.welfare.infrastructure.InterestRepository;
import com.welfare.welfare.infrastructure.LifeStageRepository;
import com.welfare.welfare.infrastructure.WelfareServiceRepository;
import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "DB_PASSWORD=test",
        "JWT_SECRET=test-jwt-secret-key-for-integration",
        "OPENAI_API_KEY=test-key",
        "WELFARE_OPEN_API_KEY=test-key"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PersonalizedWelfareIntegrationTest {

    private static final String PASSWORD = "password12";
    private static final String NAME = "홍길동";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1998, 5, 12);
    private static final String REGION = "서울특별시 강남구";
    private static final String HOUSEHOLD_CODE = "04";
    private static final String HOUSEHOLD_NAME = "한부모";
    private static final String INTEREST_CODE = "05";
    private static final String INTEREST_NAME = "주거";
    private static final String SERVICE_NAME = "청년 월세 지원";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LifeStageRepository lifeStageRepository;

    @Autowired
    private HouseholdTypeRepository householdTypeRepository;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private WelfareServiceRepository welfareServiceRepository;

    private String lifeStageCode;
    private String lifeStageName;

    @BeforeEach
    void setUp() {
        int age = LifeStageAgeMapper.ageOf(BIRTH_DATE, LocalDate.now());
        lifeStageCode = LifeStageAgeMapper.toCode(age);
        lifeStageName = "청년";
        if (lifeStageRepository.findByCode(lifeStageCode).isEmpty()) {
            lifeStageRepository.save(lifeStage(lifeStageCode, lifeStageName));
        }
        if (householdTypeRepository.findByCode(HOUSEHOLD_CODE).isEmpty()) {
            householdTypeRepository.save(householdType(HOUSEHOLD_CODE, HOUSEHOLD_NAME));
        }
        if (interestRepository.findByCode(INTEREST_CODE).isEmpty()) {
            interestRepository.save(interest(INTEREST_CODE, INTEREST_NAME));
        }
    }

    @DisplayName("프로필이 있으면 맞춤 복지 목록을 점수와 함께 조회한다.")
    @Test
    void searchPersonalizedWelfareWithProfile() throws Exception {
        saveMatchingWelfare();
        String accessToken = signupAndLogin("with-profile@example.com");
        saveProfile(accessToken);

        mockMvc.perform(get("/api/welfare/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].servNm").value(SERVICE_NAME))
                .andExpect(jsonPath("$.data.content[0].score").value(16))
                .andExpect(jsonPath("$.data.content[0].matchReasons[0]").value(REGION))
                .andExpect(jsonPath("$.data.content[0].matchReasons[1]").value(lifeStageName))
                .andExpect(jsonPath("$.data.content[0].matchReasons[2]").value(INTEREST_NAME))
                .andExpect(jsonPath("$.data.content[0].matchReasons[3]").value(HOUSEHOLD_NAME));
    }

    @DisplayName("프로필이 없으면 맞춤 복지 조회는 실패한다.")
    @Test
    void searchPersonalizedWelfareWithoutProfile() throws Exception {
        String accessToken = signupAndLogin("without-profile@example.com");

        mockMvc.perform(get("/api/welfare/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("PROFILE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("저장된 프로필이 없습니다."));
    }

    private void saveMatchingWelfare() {
        LifeStage lifeStage = lifeStageRepository.findByCode(lifeStageCode).orElseThrow();
        HouseholdType householdType = householdTypeRepository.findByCode(HOUSEHOLD_CODE).orElseThrow();
        Interest interest = interestRepository.findByCode(INTEREST_CODE).orElseThrow();

        WelfareService service = WelfareService.builder()
                .apiCd("NAW02")
                .servCd("W001")
                .servNm(SERVICE_NAME)
                .ctpvNm("서울특별시")
                .sggNm("강남구")
                .inqNum(10)
                .build();
        service.replaceLifeStages(Set.of(lifeStage));
        service.replaceHouseholdTypes(Set.of(householdType));
        service.replaceInterests(Set.of(interest));
        welfareServiceRepository.save(service);
    }

    private String signupAndLogin(String email) throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignupBody(email, PASSWORD, NAME))))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginBody(email, PASSWORD))))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode data = objectMapper.readTree(result.getResponse().getContentAsString()).path("data");
        return data.path("accessToken").asText();
    }

    private void saveProfile(String accessToken) throws Exception {
        ProfileBody profile = new ProfileBody(
                BIRTH_DATE,
                REGION,
                List.of(INTEREST_CODE),
                List.of(HOUSEHOLD_CODE),
                "차상위",
                "F"
        );
        mockMvc.perform(put("/api/users/me/profile")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isOk());
    }

    private LifeStage lifeStage(String code, String name) {
        LifeStage lifeStage = instantiate(LifeStage.class);
        ReflectionTestUtils.setField(lifeStage, "code", code);
        ReflectionTestUtils.setField(lifeStage, "name", name);
        return lifeStage;
    }

    private HouseholdType householdType(String code, String name) {
        HouseholdType householdType = instantiate(HouseholdType.class);
        ReflectionTestUtils.setField(householdType, "code", code);
        ReflectionTestUtils.setField(householdType, "name", name);
        return householdType;
    }

    private Interest interest(String code, String name) {
        Interest interest = instantiate(Interest.class);
        ReflectionTestUtils.setField(interest, "code", code);
        ReflectionTestUtils.setField(interest, "name", name);
        return interest;
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

    private record SignupBody(String email, String password, String name) {
    }

    private record LoginBody(String email, String password) {
    }

    private record ProfileBody(
            LocalDate birthDate,
            String region,
            List<String> interestCodes,
            List<String> householdTypeCodes,
            String incomeLevel,
            String gender
    ) {
    }
}
