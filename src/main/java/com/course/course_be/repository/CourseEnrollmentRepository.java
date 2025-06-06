package com.course.course_be.repository;

import com.course.course_be.dto.response.account.CourseProgressResponse;
import com.course.course_be.dto.response.account.MyCourseResponse;
import com.course.course_be.entity.CourseEnrollment;
import com.course.course_be.entity.RefreshToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Integer> {

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

    @Query("""
            SELECT new com.course.course_be.dto.response.account.MyCourseResponse(
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
    Page<MyCourseResponse> findMyCourse(
            @Param("accountId") String accountId,
            @Param("courseName") String courseName,
            @Param("status") String status,
            Pageable pageable
    );

    Page<CourseEnrollment> findByAccount_Profile_NameContainingIgnoreCaseAndCourse_NameContainingIgnoreCaseAndStatusNot(
            String accountName, String CourseName, String status, Pageable pageable);

//    Page<CourseEnrollment> findByCourse_IdAndParentCourseEnrollmentIsNullAndStatusNot(String course_id, String status, Pageable pageable);
//
//    Page<CourseEnrollment> findByLesson_IdAndParentCourseEnrollmentIsNullAndStatusNot(String lesson_id, String status, Pageable pageable);
//
//    Page<CourseEnrollment> findByParentCourseEnrollment_IdAndStatusNot(Integer parentCourseEnrollment_id, String status, Pageable pageable);
}
