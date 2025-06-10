package com.course.course_be.service;

import com.course.course_be.dto.response.lessonclient.ChapterSidebarResponse;
import com.course.course_be.dto.response.lessonclient.CourseInfoResponse;
import com.course.course_be.dto.response.lessonclient.LessonClientDetailResponse;
import com.course.course_be.dto.response.lessonclient.LessonSidebarResponse;
import com.course.course_be.entity.*;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.ChapterErrorCode;
import com.course.course_be.exception.CourseErrorCode;
import com.course.course_be.exception.LessonErrorCode;
import com.course.course_be.mapper.ChapterMapper;
import com.course.course_be.mapper.CourseMapper;
import com.course.course_be.mapper.LessonMapper;
import com.course.course_be.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonService {
    CourseRepository courseRepository;
    CourseMapper courseMapper;
    ChapterMapper chapterMapper;
    LessonMapper lessonMapper;
    LessonProgressRepository lessonProgressRepository;
    LessonRepository lessonRepository;
    SubmissionRepository submissionRepository;
    AuthenticationService authenticationService;
    public CourseInfoResponse getCourseInfo(String lessonId) {
       Account account = authenticationService
               .getMyAccountCurrent();
        Course course = new Course();
        System.out.println(" account >>> " + account.getStatus());
       if (account.getRole().equals("ADMIN")) {

           course = courseRepository.findCourseByLessonIdWithChaptersAndLessons(lessonId).
                    orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
           System.out.println("course admin >>> " + course.getName());
       } else {
           course = courseRepository.findCourseByLessonIdWithChaptersAndLessonsAndAccountId(lessonId, account.getId() )
                   .orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
           System.out.println("course client >>> " + course.getName());
       }
        System.out.println("course >>> " + course.getName());
        CourseInfoResponse courseInfoResponse = courseMapper.toCourseInfoResponse(course);

        List<ChapterSidebarResponse> chapterSidebarResponses = new ArrayList<>();

        System.out.println("size chapters >>> " + course.getChapters().size());

        List<String> allLessonIds = new ArrayList<>();
        for (Chapter chapter : course.getChapters()) {
            for (Lesson lesson : chapter.getLessons()) {
                allLessonIds.add(lesson.getId());
            }
        }

        Set<String> viewedLessonIds = new HashSet<>(lessonProgressRepository.findViewedLessonIds(account.getId(), allLessonIds));

        for (Chapter chapterItem: course.getChapters() ) {
            ChapterSidebarResponse chapterSidebarResponse = chapterMapper.toChapterSidebarResponse(chapterItem);
            chapterSidebarResponses.add(chapterSidebarResponse);
            List<LessonSidebarResponse> lessonSidebars = new ArrayList<>();

            for (Lesson lessonItem: chapterItem.getLessons() ) {
                LessonSidebarResponse lessonSidebarResponse = lessonMapper.toLessonSidebarResponse(lessonItem);

                lessonSidebarResponse.setViewed(viewedLessonIds.contains(lessonItem.getId()));

                lessonSidebars.add(lessonSidebarResponse);
            }
            chapterSidebarResponse.setLessons(lessonSidebars);
        }
        courseInfoResponse.setChapters(chapterSidebarResponses);

        return courseInfoResponse;
    }


    //introspectCourseEnrollment
    public void introspectCourseEnrollment (String lessonId) {
        var context = SecurityContextHolder.getContext();
        String accountId = context.getAuthentication().getName();

        boolean exist = courseRepository.existsByLessonIdAndAccountId(lessonId, accountId);
        if (!exist) {
            throw new AppException(CourseErrorCode.COURSE_NOT_FOUND);
        }
    }

    public LessonClientDetailResponse getLesson (String lessonId) {
        Account account = authenticationService.getMyAccountCurrent();
        Lesson lesson = new Lesson();
        if (account.getRole().equals("ADMIN")) {
            lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
        } else {
            lesson = lessonRepository.findAuthorizedLessonById(lessonId,account.getId())
                    .orElseThrow(() -> new AppException(LessonErrorCode.LESSON_NOT_FOUND));
        }

        LessonClientDetailResponse lessonClientDetailResponse =   lessonMapper.toLessonClientDetailResponse(lesson);
        Submission submission = submissionRepository.findByLessonIdAndAccountSubmitterId(lesson.getId(), account.getId());

        if (submission != null) {
        lessonClientDetailResponse.setSubmissionUrl(submission.getSubmissionUrl());
        } else {
            lessonClientDetailResponse.setSubmissionUrl(null);
        }
        return lessonClientDetailResponse;
    }
}


