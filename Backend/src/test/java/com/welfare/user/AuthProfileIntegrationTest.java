package com.welfare.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.welfare.user.domain.LifeStageAgeMapper;
import com.welfare.welfare.domain.HouseholdType;
import com.welfare.welfare.domain.Interest;
import com.welfare.welfare.domain.LifeStage;
import com.welfare.welfare.infrastructure.HouseholdTypeRepository;
import com.welfare.welfare.infrastructure.InterestRepository;
import com.welfare.welfare.infrastructure.LifeStageRepository;
import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.List;
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
class AuthProfileIntegrationTest {

    private static final String EMAIL = "profile-flow@example.com";
    private static final String PASSWORD = "password12";
    private static final String NAME = "홍길동";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1998, 5, 12);
    private static final String REGION = "서울특별시 강남구";
    private static final String HOUSEHOLD_CODE = "04";
    private static final String HOUSEHOLD_NAME = "한부모";
    private static final String INTEREST_CODE = "05";
    private static final String INTEREST_NAME = "주거";

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

    @DisplayName("회원가입 후 로그인하고, 발급된 토큰으로 프로필을 저장한 뒤 조회한다.")
    @Test
    void signupLoginAndSaveProfile() throws Exception {
        String userId = signup();
        String accessToken = login();

        mockMvc.perform(put("/api/users/me/profile")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileBody())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("프로필이 저장되었습니다."))
                .andExpect(jsonPath("$.data.userId").value(userId));

        int age = LifeStageAgeMapper.ageOf(BIRTH_DATE, LocalDate.now());
        mockMvc.perform(get("/api/users/me/profile")
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.birthDate").value(BIRTH_DATE.toString()))
                .andExpect(jsonPath("$.data.age").value(age))
                .andExpect(jsonPath("$.data.gender").value("F"))
                .andExpect(jsonPath("$.data.region").value(REGION))
                .andExpect(jsonPath("$.data.incomeLevel").value("차상위"))
                .andExpect(jsonPath("$.data.lifeStages[0].code").value(lifeStageCode))
                .andExpect(jsonPath("$.data.lifeStages[0].name").value(lifeStageName))
                .andExpect(jsonPath("$.data.householdTypes[0].code").value(HOUSEHOLD_CODE))
                .andExpect(jsonPath("$.data.householdTypes[0].name").value(HOUSEHOLD_NAME))
                .andExpect(jsonPath("$.data.interests[0].code").value(INTEREST_CODE))
                .andExpect(jsonPath("$.data.interests[0].name").value(INTEREST_NAME));
    }

    @DisplayName("가입되지 않은 이메일로 로그인하면 실패한다.")
    @Test
    void loginWithUnknownUser() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginBody("missing@example.com", PASSWORD))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    @DisplayName("틀린 비밀번호로 로그인하면 실패한다.")
    @Test
    void loginWithWrongPassword() throws Exception {
        signup();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginBody(EMAIL, "wrong-password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    private String signup() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignupBody(EMAIL, PASSWORD, NAME))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
                .andExpect(jsonPath("$.data.email").value(EMAIL))
                .andExpect(jsonPath("$.data.name").value(NAME))
                .andReturn();

        return readData(result, "userId");
    }

    private String login() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginBody(EMAIL, PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.email").value(EMAIL))
                .andExpect(jsonPath("$.data.name").value(NAME))
                .andReturn();

        return readData(result, "accessToken");
    }

    private String readData(MvcResult result, String field) throws Exception {
        JsonNode data = objectMapper.readTree(result.getResponse().getContentAsString()).path("data");
        return data.path(field).asText();
    }

    private ProfileBody profileBody() {
        return new ProfileBody(
                BIRTH_DATE,
                REGION,
                List.of(INTEREST_CODE),
                List.of(HOUSEHOLD_CODE),
                "차상위",
                "F"
        );
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
