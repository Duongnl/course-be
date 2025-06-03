package com.course.course_be.repository;

import com.course.course_be.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {
    boolean existsByGoogleId(String facebookId);
    Account findByGoogleId(String googleId);
    Optional<Account> findByGoogleIdAndStatus(String googleId, String status);
    Account findByUsername(String username);

}
