package com.course.course_be.repository;

import com.course.course_be.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseEnrollmentRepository extends JpaRepository<RefreshToken, Integer> {

}
