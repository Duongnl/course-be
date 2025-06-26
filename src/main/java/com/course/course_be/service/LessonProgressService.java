package com.course.course_be.service;

import com.course.course_be.dto.response.lesson_progress.LessonProgressResponse;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.Lesson;
import com.course.course_be.entity.LessonProgress;
import com.course.course_be.exception.*;
import com.course.course_be.mapper.LessonProgressMapper;
import com.course.course_be.repository.CourseEnrollmentRepository;
import com.course.course_be.repository.CourseRepository;
import com.course.course_be.repository.LessonProgressRepository;
import com.course.course_be.repository.LessonRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonProgressService {

    LessonProgressMapper lessonProgressMapper;
    LessonProgressRepository lessonProgressRepository;
    CourseRepository courseRepository;
    AuthenticationService authenticationService;
    LessonRepository lessonRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;

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

    public LessonProgressResponse getLessonProgressLatestOfCourseForUser (String courseId) {
        var context = SecurityContextHolder.getContext();
        String accountId = context.getAuthentication().getName();
        if(accountId == null) {
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }
        if(!courseEnrollmentRepository.checkIsEnrollmentCourse(courseId, accountId)){
            throw new AppException(LessonProgressErrorCode.LESSON_PROGRESS_FORBIDDEN);
        }
        LessonProgress latest = lessonProgressRepository
                .findLatestProgress(courseId, accountId, PageRequest.of(0, 10))
                .stream()
                .findFirst()
                .orElse(null);

        if (latest != null) {
            return lessonProgressMapper.toLessonProgressResponse(latest);
        }
        // Nếu chưa có progress, tìm bài học đầu tiên của khóa học
        Lesson firstLesson = lessonRepository
                .findFirstLessonInCourse(courseId, Pageable.unpaged())
                .stream()
                .findFirst()
                .orElseThrow(() -> new AppException(LessonErrorCode.LESSON_NOT_FOUND));

        return LessonProgressResponse.builder()
                .lessonId(firstLesson.getId())
                .build();
    }
}
