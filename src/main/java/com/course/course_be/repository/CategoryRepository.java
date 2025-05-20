package com.course.course_be.repository;

import com.course.course_be.entity.Category;
import com.course.course_be.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
