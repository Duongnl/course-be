package com.course.course_be.repository;

import com.course.course_be.entity.Category;
import com.course.course_be.entity.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    @Query("""
                SELECT c FROM Chapter c
                WHERE (:name IS NULL OR :name = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
                  AND (:detail IS NULL OR :detail = '' OR LOWER(c.detail) LIKE LOWER(CONCAT('%', :detail, '%')))
                  AND (
                    (:status IS NULL OR :status = '') AND c.status <> 'deleted'
                    OR (:status IS NOT NULL AND :status <> '' AND c.status IN :statusList)
                  )
                  AND (:courseId IS NULL OR :courseId = '' OR c.course.id = :courseId )
            """)
    Page<Chapter> searchChapters(
            @Param("name") String name,
            @Param("detail") String detail,
            @Param("status") String status,
            @Param("statusList") List<String> statusList,
            @Param("courseId") String courseId,
            Pageable pageable
    );
    Optional<Chapter> findByIdAndCourseId(Integer chapterId, String courseId);
}
