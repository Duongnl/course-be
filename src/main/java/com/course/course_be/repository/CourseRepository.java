package com.course.course_be.repository;

import com.course.course_be.entity.Account;
import com.course.course_be.entity.Category;
import com.course.course_be.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String> {

    //    @Query("""
//    SELECT DISTINCT c FROM Course c
//    JOIN FETCH c.chapters ch
//    JOIN FETCH ch.lessons l
//    JOIN CourseEnrollment ce ON ce.course = c
//    WHERE l.id = :lessonId
//      AND ce.account.id = :accountId
//""")
    @Query("""
                    SELECT DISTINCT c FROM Course c
                    JOIN FETCH c.chapters ch
                    JOIN FETCH ch.lessons l
                    JOIN CourseEnrollment ce ON ce.course = c
                    WHERE c.id = (
                        SELECT c2.id FROM Course c2
                        JOIN c2.chapters ch2
                        JOIN ch2.lessons l2
                        WHERE l2.id = :lessonId
                    )
                    AND ce.account.id = :accountId
            """)
    Optional<Course> findCourseByLessonIdWithChaptersAndLessonsAndAccountId(
            @Param("lessonId") String lessonId,
            @Param("accountId") String accountId);


    @Query("""
                SELECT 
                    CASE WHEN EXISTS (
                        SELECT 1 FROM Course c
                        JOIN c.chapters ch
                        JOIN ch.lessons l
                        JOIN CourseEnrollment ce ON ce.course = c
                        WHERE l.id = :lessonId AND ce.account.id = :accountId
                    ) THEN true ELSE false
                END
            """)
    boolean existsByLessonIdAndAccountId(
            @Param("lessonId") String lessonId,
            @Param("accountId") String accountId);


    // tìm kiếm khóa học theo tên
    Page<Course> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("""
            SELECT c FROM Course c
            LEFT JOIN c.courseEnrollments ce
            WHERE c.status = 'active'
            GROUP BY c
            ORDER BY COUNT(ce) DESC
            """)
    List<Course> getHotCourse(Pageable pageable);

    @Query("""
                SELECT c FROM Course c
                WHERE c.status = 'active'
                ORDER BY c.createdAt DESC
            """)
    List<Course> getNewestCourse(Pageable pageable);


    List<Course> findByCategoryAndStatus(Category category, String status, Pageable pageable);

}
