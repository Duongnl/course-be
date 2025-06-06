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
      AND s.accountSubmitter.username LIKE %:submitterUsername%
      AND s.accountSubmitter.profile.name LIKE %:submitterName%
      AND s.status LIKE %:status%
      AND s.status <> 'deleted'
      AND (
          (:from IS NULL AND :to IS NULL) OR (s.submittedAt BETWEEN :from AND :to)
      )
      ORDER BY s.submittedAt DESC
""")
    Page<Submission> filterSubmissionsAdminWithDateSort(
            @Param("courseName") String courseName,
            @Param("lessonName") String lessonName,
            @Param("submitterUsername") String submitterUsername,
            @Param("submitterName") String submitterName,
            @Param("status") String status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );


    @Query("""
    SELECT s FROM Submission s
    WHERE s.lesson.chapter.course.name LIKE %:courseName%
      AND s.lesson.name LIKE %:lessonName%
      AND s.accountSubmitter.username LIKE %:submitterUsername%
      AND s.accountSubmitter.profile.name LIKE %:submitterName%
      AND s.status LIKE %:status%
      AND s.status <> 'deleted'
      AND (
          (:from IS NULL AND :to IS NULL) OR (s.submittedAt BETWEEN :from AND :to)
      )
""")
    Page<Submission> filterSubmissionsAdminWithoutOrder(
            @Param("courseName") String courseName,
            @Param("lessonName") String lessonName,
            @Param("submitterUsername") String submitterUsername,
            @Param("submitterName") String submitterName,
            @Param("status") String status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );


    @Query("""
    SELECT s FROM Submission s
    WHERE s.lesson.chapter.course.name LIKE %:courseName%
      AND s.lesson.name LIKE %:lessonName%
      AND s.accountSubmitter.id = :accountId
      AND s.status LIKE %:status%
      AND s.status <> 'deleted'
      AND (
          (:from IS NULL AND :to IS NULL) OR (s.submittedAt BETWEEN :from AND :to)
      )
      ORDER BY s.submittedAt DESC
""")
    Page<Submission> filterSubmissionsClientWithDateSort(
            @Param("courseName") String courseName,
            @Param("lessonName") String lessonName,
            @Param("accountId") String accountId,
            @Param("status") String status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );


    @Query("""
    SELECT s FROM Submission s
    WHERE s.lesson.chapter.course.name LIKE %:courseName%
      AND s.lesson.name LIKE %:lessonName%
      AND s.accountSubmitter.id = :accountId
      AND s.status LIKE %:status%
      AND s.status <> 'deleted'
      AND (
          (:from IS NULL AND :to IS NULL) OR (s.submittedAt BETWEEN :from AND :to)
      )
""")
    Page<Submission> filterSubmissionsClientWithoutOrder(
            @Param("courseName") String courseName,
            @Param("lessonName") String lessonName,
            @Param("accountId") String accountId,
            @Param("status") String status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );





}
