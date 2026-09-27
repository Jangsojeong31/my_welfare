package com.welfare.user.infrastructure;

import com.welfare.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"profile", "lifeStages", "householdTypes", "interests"})
    @Query("select distinct u from User u where u.id = :id")
    Optional<User> findDetailById(@Param("id") String id);
}
