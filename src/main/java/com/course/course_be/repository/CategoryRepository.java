package com.course.course_be.repository;

import com.course.course_be.entity.Category;
import com.course.course_be.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    @Query("""
    SELECT c FROM Category c
    WHERE c.name LIKE %:name%
      AND (
        (:detail = '' AND (c.detail IS NULL OR c.detail = ''))
        OR (:detail != '' AND c.detail LIKE %:detail%)
      )
      AND c.status <> :statusNot
""")
    Page<Category> findByNameContainingAndDetailContainingAndStatusNot(
            @Param("name") String name,
            @Param("detail") String detail,
            @Param("statusNot") String statusNot,
            Pageable pageable);

    @Query("""
    SELECT c FROM Category c
    WHERE c.name LIKE %:name%
      AND (
        (:detail = '' AND (c.detail IS NULL OR c.detail = ''))
        OR (:detail != '' AND c.detail LIKE %:detail%)
      )
      AND c.status IN :statusList
""")
    Page<Category> findByNameContainingAndDetailContainingAndStatusIn(
            @Param("name") String name,
            @Param("detail") String detail,
            @Param("statusList") List<String> statusList,
            Pageable pageable);

    @Query("""
    SELECT c FROM Category c
    WHERE c.name LIKE %:name%
      AND (
        (:detail = '' AND (c.detail IS NULL OR c.detail = ''))
        OR (:detail != '' AND c.detail LIKE %:detail%)
      )
      AND c.status IN :statusList
""")
    List<Category> findByNameContainingAndDetailContainingAndStatusIn(
            String name, String detail, List<String> status, Sort sort);

    @Query("""
    SELECT c FROM Category c
    WHERE c.name LIKE %:name%
      AND (
        (:detail = '' AND (c.detail IS NULL OR c.detail = ''))
        OR (:detail != '' AND c.detail LIKE %:detail%)
      )
      AND c.status <> :statusNot
""")
    List<Category> findByNameContainingAndDetailContainingAndStatusNot(
            String name, String detail, String statusNot, Sort sort);
}
