package com.course.course_be.repository;

import com.course.course_be.entity.Account;
import com.course.course_be.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String> {
    // tìm kiếm khóa học theo tên
    Page<Course> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

}
