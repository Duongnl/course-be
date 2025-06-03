package com.course.course_be.repository;

import com.course.course_be.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Page<Category> findByNameContainingAndStatusIn(String name, List<String> status, Pageable pageable);

    Page<Category> findByNameContainingAndDetailContainingAndStatusIn(
            String name,
            String detail,
            List<String> statusList,
            Pageable pageable);

    Page<Category> findByNameContainingAndStatusNot(
            String name,
            String statusNot,
            Pageable pageable);

    Page<Category> findByNameContainingAndDetailContainingAndStatusNot(
            String name,
            String detail,
            String statusNot,
            Pageable pageable);

}
