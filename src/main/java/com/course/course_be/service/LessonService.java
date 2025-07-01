package com.course.course_be.service;

import com.course.course_be.dto.request.lesson.CreateLessonRequest;
import com.course.course_be.dto.request.lesson.UpdateLessonRequest;
import com.course.course_be.dto.response.lesson.LessonResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

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
    ChapterRepository chapterRepository;

    public CourseInfoResponse getCourseInfo(String lessonId) {
        Account account = authenticationService
                .getMyAccountCurrent();
        Course course = new Course();
        if (account.getRole().equals("ADMIN")) {

            course = courseRepository.findCourseByLessonIdWithChaptersAndLessons(lessonId).
                    orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
       
        } else {
            course = courseRepository.findCourseByLessonIdWithChaptersAndLessonsAndAccountId(lessonId, account.getId())
                    .orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
         
        }
      
        CourseInfoResponse courseInfoResponse = courseMapper.toCourseInfoResponse(course);

        List<ChapterSidebarResponse> chapterSidebarResponses = new ArrayList<>();

      

        List<String> allLessonIds = new ArrayList<>();
        for (Chapter chapter : course.getChapters()) {
            for (Lesson lesson : chapter.getLessons()) {
                allLessonIds.add(lesson.getId());
            }
        }

        Set<String> viewedLessonIds = new HashSet<>(lessonProgressRepository.findViewedLessonIds(account.getId(), allLessonIds));

        for (Chapter chapterItem : course.getChapters()) {
            if (chapterItem.getStatus().equals("active")) {
                ChapterSidebarResponse chapterSidebarResponse = chapterMapper.toChapterSidebarResponse(chapterItem);
                chapterSidebarResponses.add(chapterSidebarResponse);
                List<LessonSidebarResponse> lessonSidebars = new ArrayList<>();

                for (Lesson lessonItem : chapterItem.getLessons()) {
                    if (lessonItem.getStatus().equals("active")) {
                        LessonSidebarResponse lessonSidebarResponse = lessonMapper.toLessonSidebarResponse(lessonItem);
                        lessonSidebarResponse.setViewed(viewedLessonIds.contains(lessonItem.getId()));
                        lessonSidebars.add(lessonSidebarResponse);
                    }
                }
                chapterSidebarResponse.setLessons(lessonSidebars);
            }
        }

        courseInfoResponse.setChapters(chapterSidebarResponses);

        return courseInfoResponse;
    }


    //introspectCourseEnrollment
    public void introspectCourseEnrollment(String lessonId) {
        var context = SecurityContextHolder.getContext();
        String accountId = context.getAuthentication().getName();

        boolean exist = courseRepository.existsByLessonIdAndAccountId(lessonId, accountId);
        if (!exist) {
            throw new AppException(CourseErrorCode.COURSE_NOT_FOUND);
        }
    }

    public LessonClientDetailResponse getLesson(String lessonId) {
        Account account = authenticationService.getMyAccountCurrent();
        Lesson lesson = new Lesson();


        if (account.getRole().equals("ADMIN")) {
            lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));

        }  else {
            lesson = lessonRepository.findAuthorizedLessonById(lessonId, account.getId())
                    .orElseThrow(() -> new AppException(LessonErrorCode.LESSON_NOT_FOUND));
        }

        LessonClientDetailResponse lessonClientDetailResponse = lessonMapper.toLessonClientDetailResponse(lesson);
        Submission submission = submissionRepository.findByLessonIdAndAccountSubmitterId(lesson.getId(), account.getId());

        if (submission != null) {
            lessonClientDetailResponse.setSubmissionUrl(submission.getSubmissionUrl());
        } else {
            lessonClientDetailResponse.setSubmissionUrl(null);
        }
        return lessonClientDetailResponse;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<LessonResponse> getAll(String name, String detail, String status, int chapterId, Pageable pageable) {
        chapterRepository.findById(chapterId).orElseThrow(() -> new AppException(ChapterErrorCode.CHAPTER_NOT_FOUND));
        name = name == null ? "" : name;
        List<String> statusList = new ArrayList<>();
        if (status != null && !status.trim().isEmpty()) {
            statusList = Arrays.asList(status.split("\\."));
        }
        Page<Lesson> page = lessonRepository.searchLessons(name, detail, status, statusList, chapterId, pageable);
        return page.map(lessonMapper::toLessonResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public LessonResponse createNew(CreateLessonRequest request) {
        Chapter chapter = chapterRepository.findById(request.getChapterId()).orElseThrow(() -> new AppException(ChapterErrorCode.CHAPTER_NOT_FOUND));
        Lesson lesson = Lesson.builder()
                .lessonNumber(request.getLessonNumber())
                .name(request.getName())
                .detail(request.getDetail())
                .videoUrl(request.getVideoUrl())
                .duration(request.getDuration())
                .documentUrl(request.getDocumentUrl())
                .assignmentUrl(request.getAssignmentUrl())
                .status("active")
                .createdAt(LocalDateTime.now())
                .chapter(chapter)
                .build();
        Lesson savedLesson = lessonRepository.save(lesson);
        return lessonMapper.toLessonResponse(savedLesson);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public LessonResponse update(String id, UpdateLessonRequest request) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new AppException(LessonErrorCode.LESSON_NOT_FOUND));
        lesson.setLessonNumber(request.getLessonNumber());
        lesson.setName(request.getName());
        lesson.setDetail(request.getDetail());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setDuration(request.getDuration());
        lesson.setDocumentUrl(request.getDocumentUrl());
        lesson.setAssignmentUrl(request.getAssignmentUrl());
        lesson.setStatus(request.getStatus());
        lesson.setUpdatedAt(LocalDateTime.now());
        Lesson updatedLesson = lessonRepository.save(lesson);
        return lessonMapper.toLessonResponse(updatedLesson);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String id) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new AppException(LessonErrorCode.LESSON_NOT_FOUND));
        lesson.setStatus("deleted");
        lessonRepository.save(lesson);
    }
}


