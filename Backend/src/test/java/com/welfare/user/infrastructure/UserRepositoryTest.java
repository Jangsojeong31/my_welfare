package com.welfare.user.infrastructure;

import com.welfare.user.domain.User;
import com.welfare.welfare.domain.LifeStage;
import jakarta.persistence.PersistenceUnitUtil;
import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
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
class UserRepositoryTest {

    private static final String EMAIL = "user@example.com";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("이메일로 회원 존재 여부와 회원을 조회한다.")
    @Test
    void findsUserByEmail() {
        entityManager.persist(User.create(EMAIL, "encoded-password", "홍길동"));
        entityManager.flush();
        entityManager.clear();

        assertTrue(userRepository.existsByEmail(EMAIL));
        assertFalse(userRepository.existsByEmail("missing@example.com"));
        assertEquals("홍길동", userRepository.findByEmail(EMAIL).orElseThrow().getName());
        assertTrue(userRepository.findByEmail("missing@example.com").isEmpty());
    }

    @DisplayName("상세 조회는 프로필과 생애주기를 함께 불러온다.")
    @Test
    void findDetailLoadsProfileAndAssociations() {
        LifeStage youth = instantiate(LifeStage.class);
        ReflectionTestUtils.setField(youth, "code", "05");
        ReflectionTestUtils.setField(youth, "name", "청년");
        entityManager.persist(youth);

        User user = User.create(EMAIL, "encoded-password", "홍길동");
        user.getOrCreateProfile().update(LocalDate.of(1998, 5, 12), "F", "서울특별시 강남구", "차상위");
        user.replaceLifeStages(Set.of(youth));
        String userId = entityManager.persist(user).getId();
        entityManager.flush();
        entityManager.clear();

        User found = userRepository.findDetailById(userId).orElseThrow();

        PersistenceUnitUtil util = entityManager.getEntityManager()
                .getEntityManagerFactory()
                .getPersistenceUnitUtil();
        assertTrue(util.isLoaded(found, "profile"));
        assertTrue(util.isLoaded(found, "lifeStages"));
        assertTrue(util.isLoaded(found, "householdTypes"));
        assertTrue(util.isLoaded(found, "interests"));
        assertEquals("서울특별시 강남구", found.getProfile().getRegionCode());
        assertEquals("청년", found.getLifeStages().iterator().next().getName());
    }

    @DisplayName("없는 회원 ID로 상세 조회하면 비어 있다.")
    @Test
    void findDetailReturnsEmptyForUnknownId() {
        Optional<User> found = userRepository.findDetailById("missing-id");

        assertTrue(found.isEmpty());
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
