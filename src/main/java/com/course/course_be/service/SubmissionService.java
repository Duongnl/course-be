package com.course.course_be.service;

import com.course.course_be.dto.request.submissionclient.SubmissionClientRequest;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.Lesson;
import com.course.course_be.entity.Submission;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.LessonErrorCode;
import com.course.course_be.exception.SubmissionErrorCode;
import com.course.course_be.repository.CourseRepository;
import com.course.course_be.repository.LessonRepository;
import com.course.course_be.repository.SubmissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionService {

    CourseRepository courseRepository;
    SubmissionRepository submissionRepository;
    LessonRepository lessonRepository;
    AuthenticationService authenticationService;


    public void submission(SubmissionClientRequest request) {
        Account account  = authenticationService.getMyAccountCurrent();

        Lesson lesson = lessonRepository.findAuthorizedLessonById(request.getLessonId(),account.getId())
                .orElseThrow(() -> new AppException(LessonErrorCode.LESSON_NOT_FOUND));

        if (lesson.getAssignmentUrl() == null) {
            throw  new AppException(SubmissionErrorCode.SUBMISSION_INVALID);
        }

        boolean submission = submissionRepository.existsByLessonIdAndAccountSubmitterId(request.getLessonId(),account.getId());
        if (submission) {
            throw  new AppException(SubmissionErrorCode.SUBMISSION_EXIST);
        }



        Submission submissionNew = new Submission();
        submissionNew.setLesson(lesson);
        submissionNew.setSubmissionUrl(request.getSubmissionUrl());
        submissionNew.setSubmittedAt(LocalDateTime.now());
        submissionNew.setStatus("submitted");
        submissionNew.setAccountSubmitter(account);
        try {

        submissionRepository.save(submissionNew);
        }catch (Exception e) {
            throw new AppException(SubmissionErrorCode.SAVE_SUBMISSION_FAIL);
        }

    }


}
