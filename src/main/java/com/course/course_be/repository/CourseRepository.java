package com.course.course_be.repository;

import com.course.course_be.entity.Account;
import com.course.course_be.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, String> {
}
