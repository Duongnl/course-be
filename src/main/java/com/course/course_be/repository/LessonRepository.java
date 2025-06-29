package com.course.course_be.repository;

import com.course.course_be.entity.Course;
import com.course.course_be.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, String> {

    @Query("""
                SELECT l FROM Lesson l
                JOIN l.chapter ch
                JOIN ch.course c
                JOIN CourseEnrollment ce ON ce.course = c
                WHERE l.id = :lessonId AND ce.account.id = :accountId  AND ce.status = 'active'
            """)
    Optional<Lesson> findAuthorizedLessonById(
            @Param("lessonId") String lessonId,
            @Param("accountId") String accountId);

    @Query("""
                SELECT l FROM Lesson l
                WHERE (:name IS NULL OR :name = '' OR LOWER(l.name) LIKE LOWER(CONCAT('%', :name, '%')))
                  AND (:detail IS NULL OR :detail = '' OR LOWER(l.detail) LIKE LOWER(CONCAT('%', :detail, '%')))
                  AND (
                    (:status IS NULL OR :status = '') AND l.status <> 'deleted'
                    OR (:status IS NOT NULL AND :status <> '' AND l.status IN :statusList)
                  )
                  AND (:chapterId IS NULL OR :chapterId = '' OR l.chapter.id = CAST(:chapterId AS integer) )
            """)
    Page<Lesson> searchLessons(@Param("name") String name,
                               @Param("detail") String detail,
                               @Param("status") String status,
                               @Param("statusList") List<String> statusList,
                               @Param("chapterId") int chapterId,
                               Pageable pageable);

    @Query("""
                SELECT l FROM Lesson l
                JOIN l.chapter ch
                JOIN ch.course c
                WHERE c.id = :courseId
                  AND l.status = 'active'
                  AND ch.status = 'active'
                  AND c.status = 'active'
                ORDER BY l.lessonNumber ASC
            """)
    List<Lesson> findFirstLessonInCourse(@Param("courseId") String courseId, Pageable pageable);


    @Query("""
            SELECT COUNT(l) > 0 FROM Lesson l
            JOIN l.chapter ch
            JOIN ch.course c
            JOIN CourseEnrollment ce ON ce.course = c
            WHERE l.videoUrl = :videoUrl
            AND l.status = 'active'
            AND ch.status = 'active'
            AND c.status = 'active'
            AND ce.account.id = :accountId
            AND ce.status = 'active'
            AND ce.account.status = 'active'
            """)
    boolean existsAuthorizedLessonByVideoUrl(
            @Param("videoUrl") String videoUrl,
            @Param("accountId") String accountId
    );


    @Query("""
            SELECT COUNT(l) > 0 FROM Lesson l
            JOIN l.chapter ch
            JOIN ch.course c
            JOIN CourseEnrollment ce ON ce.course = c
            WHERE l.assignmentUrl = :assignmentUrl
            AND l.status = 'active'
            AND ch.status = 'active'
            AND c.status = 'active'
            AND ce.account.id = :accountId
            AND ce.status = 'active'
            AND ce.account.status = 'active'
            """)
    boolean existsAuthorizedLessonByAssignmentUrl(
            @Param("assignmentUrl") String assignmentUrl,
            @Param("accountId") String accountId
    );


    @Query("""
            SELECT COUNT(l) > 0 FROM Lesson l
            JOIN l.chapter ch
            JOIN ch.course c
            JOIN CourseEnrollment ce ON ce.course = c
            WHERE l.documentUrl = :documentUrl
            AND l.status = 'active'
            AND ch.status = 'active'
            AND c.status = 'active'
            AND ce.account.id = :accountId
            AND ce.status = 'active'
            AND ce.account.status = 'active'
            """)
    boolean existsAuthorizedLessonByDocumentUrl(
            @Param("documentUrl") String documentUrl,
            @Param("accountId") String accountId
    );

}
