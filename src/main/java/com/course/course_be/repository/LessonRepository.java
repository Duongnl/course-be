package com.course.course_be.repository;

import com.course.course_be.entity.Course;
import com.course.course_be.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, String> {

    @Query("""
    SELECT l FROM Lesson l
    JOIN l.chapter ch
    JOIN ch.course c
    JOIN CourseEnrollment ce ON ce.course = c
    WHERE l.id = :lessonId AND ce.account.id = :accountId
""")
    Optional<Lesson> findAuthorizedLessonById(
            @Param("lessonId") String lessonId,
            @Param("accountId") String accountId);

}
