package com.course.course_be.service;

import com.course.course_be.entity.Account;
import com.course.course_be.entity.Lesson;
import com.course.course_be.entity.LessonProgress;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.CommentErrorCode;
import com.course.course_be.exception.CourseErrorCode;
import com.course.course_be.exception.LessonErrorCode;
import com.course.course_be.repository.CourseRepository;
import com.course.course_be.repository.LessonProgressRepository;
import com.course.course_be.repository.LessonRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonProgressService {

    LessonProgressRepository lessonProgressRepository;
    CourseRepository courseRepository;
    AuthenticationService authenticationService;
    LessonRepository lessonRepository;

    public void viewedLessonProgress (String lessonId) {
        var context = SecurityContextHolder.getContext();
        String accountId = context.getAuthentication().getName();
        LessonProgress lessonProgress = lessonProgressRepository.findByAccountIdAndLessonId(accountId, lessonId);
        if (lessonProgress == null) {
            if(courseRepository.existsByLessonIdAndAccountId(lessonId, accountId)) {
                lessonProgress = new LessonProgress();
                Account account = authenticationService.getMyAccountCurrent();
                Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                        () -> new AppException(LessonErrorCode.LESSON_NOT_FOUND)
                );
                lessonProgress.setAccount(account);
                lessonProgress.setLesson(lesson);
                lessonProgress.setStatus("viewed");
                lessonProgress.setWatchedDuration(200);
                lessonProgress.setViewedAt(LocalDateTime.now());
                lessonProgressRepository.save(lessonProgress);
            } else {
                throw new AppException(CourseErrorCode.COURSE_NOT_FOUND);
            }

        } else {
            lessonProgress.setViewedAt(LocalDateTime.now());
            lessonProgressRepository.save(lessonProgress);
        }
    }
}
