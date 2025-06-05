package com.course.course_be.repository;

import com.course.course_be.dto.response.account.CourseProgressResponse;
import com.course.course_be.entity.RefreshToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseEnrollmentRepository extends JpaRepository<RefreshToken, Integer> {

    @Query("""
            SELECT new com.course.course_be.dto.response.account.CourseProgressResponse(
                c.id,
                c.name,
                COUNT(DISTINCT lp.id),
                COUNT(DISTINCT l.id),
                ce.enrolledAt,
                CASE 
                    WHEN COUNT(DISTINCT lp.id) = COUNT(DISTINCT l.id) THEN 'completed'
                    ELSE 'inProgress'
                END
            )
            FROM CourseEnrollment ce
            JOIN ce.course c
            LEFT JOIN c.chapters ch
            LEFT JOIN ch.lessons l
            LEFT JOIN l.lessonProgresses lp ON lp.account.id = :accountId
            WHERE ce.account.id = :accountId
              AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :courseName, '%')))
            GROUP BY c.id, c.name, ce.enrolledAt
            HAVING (
                :status IS NULL
                OR (:status = 'completed' AND COUNT(DISTINCT lp.id) = COUNT(DISTINCT l.id))
                OR (:status = 'inProgress' AND COUNT(DISTINCT lp.id) <> COUNT(DISTINCT l.id))
            )
            ORDER BY ce.enrolledAt DESC
            """)
    Page<CourseProgressResponse> findCourseProgress(
            @Param("accountId") String accountId,
            @Param("courseName") String courseName,
            @Param("status") String status,
            Pageable pageable
    );


}
