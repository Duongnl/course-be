package com.course.course_be.repository;

import com.course.course_be.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {
    boolean existsByGoogleId(String facebookId);
    Account findByGoogleId(String googleId);
    Optional<Account> findByGoogleIdAndStatus(String googleId, String status);
    Account findByUsername(String username);

    Optional<Account> findByIdAndStatusNot(String id, String status);

    List<Account> findByStatusNot(String status);

    Optional<Account> findByEmailAndStatusNot(String email, String status);

    Optional<Account> findByUsernameAndStatusNot(String username, String status);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmailAndIdNot(String email, String id);

    boolean existsByUsernameAndIdNot(String username, String id);

    List<Account> findByRoleAndStatusNot(String role, String status);

    List<Account> findByStatus(String status);

    @Query("SELECT a FROM Account a WHERE a.status != :status")
    Page<Account> findByStatusNot(String status, Pageable pageable);
}
