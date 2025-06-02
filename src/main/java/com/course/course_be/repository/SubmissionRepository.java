package com.course.course_be.repository;

import com.course.course_be.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, String> {
    Submission findByLessonIdAndAccountSubmitterId(String submissionId, String accountSubmitterId);
    boolean existsByLessonIdAndAccountSubmitterId(String lessonId, String accountSubmitterId);



    @Query("""
    SELECT s FROM Submission s
    WHERE s.lesson.chapter.course.name LIKE %:courseName%
      AND s.lesson.name LIKE %:lessonName%
      AND (
          (:submitterEmail = '') AND (s.accountSubmitter.email IS NULL OR s.accountSubmitter.email LIKE '%')
          OR
          (:submitterEmail <> '') AND (s.accountSubmitter.email LIKE %:submitterEmail%)
      )
      AND s.accountSubmitter.profile.name LIKE %:submitterName%
      AND s.status LIKE %:status%
      AND (
          (:from IS NULL AND :to IS NULL) OR (s.submittedAt BETWEEN :from AND :to)
      )
      ORDER BY s.submittedAt DESC
""")
    Page<Submission> filterSubmissions(
            @Param("courseName") String courseName,
            @Param("lessonName") String lessonName,
            @Param("submitterEmail") String submitterEmail,
            @Param("submitterName") String submitterName,
            @Param("status") String status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

}
