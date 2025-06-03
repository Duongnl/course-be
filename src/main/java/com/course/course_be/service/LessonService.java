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
    public CourseInfoResponse getCourseInfo(String lessonId) {
        var context = SecurityContextHolder.getContext();
        String Id = context.getAuthentication().getName();

        Course course = courseRepository.findCourseByLessonIdWithChaptersAndLessonsAndAccountId(lessonId, Id )
                .orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));

        CourseInfoResponse courseInfoResponse = courseMapper.toCourseInfoResponse(course);

        List<ChapterSidebarResponse> chapterSidebarResponses = new ArrayList<>();

        System.out.println("size chapters >>> " + course.getChapters().size());

        List<String> allLessonIds = new ArrayList<>();
        for (Chapter chapter : course.getChapters()) {
            for (Lesson lesson : chapter.getLessons()) {
                allLessonIds.add(lesson.getId());
            }
        }

        Set<String> viewedLessonIds = new HashSet<>(lessonProgressRepository.findViewedLessonIds(Id, allLessonIds));

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
        var context = SecurityContextHolder.getContext();
        String accountId = context.getAuthentication().getName();
        Lesson lesson = lessonRepository.findAuthorizedLessonById(lessonId,accountId)
                .orElseThrow(() -> new AppException(LessonErrorCode.LESSON_NOT_FOUND));
        LessonClientDetailResponse lessonClientDetailResponse =   lessonMapper.toLessonClientDetailResponse(lesson);
        Submission submission = submissionRepository.findByLessonIdAndAccountSubmitterId(lesson.getId(), accountId);

        if (submission != null) {
        lessonClientDetailResponse.setSubmissionUrl(submission.getSubmissionUrl());
        } else {
            lessonClientDetailResponse.setSubmissionUrl(null);
        }
        return lessonClientDetailResponse;
    }
}


