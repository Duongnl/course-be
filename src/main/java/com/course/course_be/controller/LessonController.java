package com.course.course_be.controller;

import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.lessonclient.CourseInfoResponse;
import com.course.course_be.dto.response.lessonclient.LessonClientDetailResponse;
import com.course.course_be.service.LessonService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/introspect-course-enrollment/{id}")
    public ApiResponse<?> introspectCourseEnrollment(@PathVariable("id") String id) {
      lessonService.introspectCourseEnrollment(id);
      return ApiResponse.builder()
                .build();
    }

    @GetMapping("/get-lesson/{id}")
    public ApiResponse<LessonClientDetailResponse> getLesson(@PathVariable("id") String id) {
        return ApiResponse.<LessonClientDetailResponse>builder()
                .result(lessonService.getLesson(id))
                .build();
    }




}
