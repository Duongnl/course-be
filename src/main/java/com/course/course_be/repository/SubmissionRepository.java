package com.course.course_be.repository;

import com.course.course_be.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, String> {
    Submission findByLessonIdAndAccountSubmitterId(String submissionId, String accountSubmitterId);
    boolean existsByLessonIdAndAccountSubmitterId(String lessonId, String accountSubmitterId);

}
