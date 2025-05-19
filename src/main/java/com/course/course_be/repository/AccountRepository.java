package com.course.course_be.repository;

import com.course.course_be.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByFacebookId(String facebookId);
    Optional<Account> findByFacebookId(String userName);
}
