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


    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmailAndIdNot(String email, String id);

    boolean existsByUsernameAndIdNot(String username, String id);

    @Query("SELECT a FROM Account a WHERE a.status != :status")
    Page<Account> findByStatusNot(String status, Pageable pageable);

    @Query("""
    SELECT a FROM Account a
    WHERE a.profile.name LIKE %:name%
      AND (
          (:username IS NULL OR :username = '')
          OR
          (a.username IS NOT NULL AND a.username LIKE %:username%)
      )
      AND (
          (:email IS NULL OR :email = '')
          OR 
          (a.email IS NOT NULL AND a.email LIKE %:email%)
      )
      AND (
          (:sex IS NULL OR :sex = '') 
          OR 
          (a.profile.sex IS NOT NULL AND a.profile.sex = :sex)
      )
      AND (
          (:role IS NULL OR :role = '') 
          OR 
          (a.role IS NOT NULL AND a.role = :role)
      )
      AND (
          (:status IS NULL OR :status = '') 
          OR 
          (a.status IS NOT NULL AND a.status = :status)
      )
      AND a.status <> 'deleted'
    ORDER BY a.email ASC
""")
    Page<Account> filterAccounts(
            @Param("name") String name,
            @Param("username") String username,
            @Param("email") String email,
            @Param("sex") String sex,
            @Param("role") String role,
            @Param("status") String status,
            Pageable pageable
    );
}
