package com.course.course_be.repository;

import com.course.course_be.entity.Account;
import com.course.course_be.entity.Course;
import com.course.course_be.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    boolean existsByRefreshToken(String refreshToken);
    RefreshToken findByRefreshToken(String RefreshToken);
}
