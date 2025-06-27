package com.course.course_be.repository;

import com.course.course_be.entity.Comment;
import com.course.course_be.entity.LessonProgress;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Integer> {

    boolean existsByAccountIdAndLessonId(String accountId, String lessonId);

    @Query("SELECT lp.lesson.id FROM LessonProgress lp WHERE lp.account.id = :accountId AND lp.lesson.id IN :lessonIds")
    List<String> findViewedLessonIds(@Param("accountId") String accountId, @Param("lessonIds") List<String> lessonIds);

    LessonProgress findByAccountIdAndLessonId(String accountId, String lessonId);

    @Query("""
    SELECT lp
    FROM LessonProgress lp
    JOIN lp.lesson l
    JOIN l.chapter ch
    JOIN ch.course c
    WHERE c.id = :courseId
      AND lp.account.id = :accountId
      AND ch.status = 'active' AND c.status = 'active' AND l.status = 'active'
    ORDER BY lp.viewedAt DESC
""")
    List<LessonProgress> findLatestProgress(
            @Param("courseId") String courseId,
            @Param("accountId") String accountId,
            Pageable pageable
    );
}
