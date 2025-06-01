package com.course.course_be.controller;

import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.homeclient.CourseSearchingResponse;
import com.course.course_be.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class CourseController {
    final CourseService courseService; // hoặc private final

    @GetMapping("/search")
    public ApiResponse<Page<CourseSearchingResponse>> searchCourses(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CourseSearchingResponse> results = courseService.searchCoursesByName(keyword, pageable);

        return ApiResponse.<Page<CourseSearchingResponse>>builder()
                .result(results)
                .build();
    }
}
