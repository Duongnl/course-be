package com.course.course_be.repository;

import com.course.course_be.entity.Category;
import com.course.course_be.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Page<Category> findByNameContainingAndDetailContainingAndStatusIn(
            String name, String detail, List<String> status, Pageable pageable);

    List<Category> findByNameContainingAndDetailContainingAndStatusIn(
            String name, String detail, List<String> status, Sort sort);

    Page<Category> findByNameContainingAndDetailContainingAndStatusNot(
            String name, String detail, String statusNot, Pageable pageable);

    List<Category> findByNameContainingAndDetailContainingAndStatusNot(
            String name, String detail, String statusNot, Sort sort);
}
