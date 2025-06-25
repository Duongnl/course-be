package com.course.course_be.controller;

import com.course.course_be.dto.request.chapter.UpdateChapterRequest;
import com.course.course_be.dto.request.lesson.CreateLessonRequest;
import com.course.course_be.dto.request.lesson.UpdateLessonRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.chapter.ChapterResponse;
import com.course.course_be.dto.response.lesson.LessonResponse;
import com.course.course_be.dto.response.lessonclient.CourseInfoResponse;
import com.course.course_be.dto.response.lessonclient.LessonClientDetailResponse;
import com.course.course_be.service.LessonService;
import com.course.course_be.utils.PageableUtil;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/lesson")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonController {
    LessonService lessonService;

    @GetMapping("/course-info/{id}")
    public ApiResponse<CourseInfoResponse> getCourseInfo(@PathVariable("id") String id) {
        return ApiResponse.<CourseInfoResponse>builder()
                .result(lessonService.getCourseInfo(id))
                .build();
    }

//    @GetMapping("/introspect-course-enrollment/{id}")
//    public ApiResponse<?> introspectCourseEnrollment(@PathVariable("id") String id) {
//        lessonService.introspectCourseEnrollment(id);
//        return ApiResponse.builder()
//                .build();
//    }

    @GetMapping("/get-lesson/{id}")
    public ApiResponse<LessonClientDetailResponse> getLesson(@PathVariable("id") String id) {
        return ApiResponse.<LessonClientDetailResponse>builder()
                .result(lessonService.getLesson(id))
                .build();
    }

    @GetMapping("/{chapterId}")
    public ApiResponse<List<LessonResponse>> getAllLesson(@PathVariable("chapterId") int chapterId,
                                                          @RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer perPage,
                                                          @RequestParam(required = false) String sort,
                                                          @RequestParam(required = false) String name,
                                                          @RequestParam(required = false) String detail,
                                                          @RequestParam(required = false) String status) {
        Pageable pageable = PageableUtil.createPageable(page, perPage, sort);
        Page<LessonResponse> pageResult = lessonService.getAll(name, detail, status, chapterId, pageable);
        return ApiResponse.<List<LessonResponse>>builder()
                .result(pageResult.stream().toList())
                .currentPage(page != null ? page : 1)
                .totalPages(pageResult.getTotalPages())
                .totalItems(pageResult.getTotalElements())
                .build();
    }

    @PostMapping
    public ApiResponse<LessonResponse> createLesson(@Valid @RequestBody CreateLessonRequest request) {
        return ApiResponse.<LessonResponse>builder().result(lessonService.createNew(request)).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<LessonResponse> updateChapter(@PathVariable String id, @Valid @RequestBody UpdateLessonRequest request) {
        return ApiResponse.<LessonResponse>builder().result(lessonService.update(id, request)).build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteLesson(@PathVariable String id) {
        lessonService.delete(id);
        return ApiResponse.<String>builder()
                .message("Lesson deleted successfully")
                .build();
    }
}
