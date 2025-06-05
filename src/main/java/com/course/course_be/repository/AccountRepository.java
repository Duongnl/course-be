package com.course.course_be.repository;

import com.course.course_be.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
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

    @Query("""
        SELECT a FROM Account a
        WHERE a.profile.name LIKE %:name%
          AND (
              (:email = '') AND (a.email IS NULL OR a.email LIKE '%')
              OR
              (:email <> '') AND (a.email LIKE %:email%)
          )
          AND (
              (:sex = '') AND (a.profile.sex IS NULL OR a.profile.sex LIKE '%')
              OR
              (:sex <> '') AND (a.profile.sex = :sex)
          )
          AND (
              (:role = '') AND (a.role IS NULL OR a.role LIKE '%')
              OR
              (:role <> '') AND (a.role = :role)
          )
          AND (
              (:status = '') AND (a.status IS NULL OR a.status LIKE '%')
              OR
              (:status <> '') AND (a.status = :status)
          )
          AND a.status <> 'deleted'
        ORDER BY a.email ASC
    """)
    Page<Account> filterAccounts(
            @Param("name") String name,
            @Param("email") String email,
            @Param("sex") String sex,
            @Param("role") String role,
            @Param("status") String status,
            Pageable pageable
    );
}
