package com.course.course_be.controller;

import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.homeclient.CourseCardResponse;
import com.course.course_be.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class CourseController {
    final CourseService courseService; // hoặc private final

    @GetMapping("/filter")
    public ApiResponse<List<CourseCardResponse>> filterCourses (@RequestParam(required = false) String sort ,
                                                                @RequestParam(required = false) String category,
                                                                @RequestParam (required = false) String keyword,
                                                                @RequestParam    int page ,
                                                                @RequestParam  int size
                                                                )
    {

        Page<CourseCardResponse> course = courseService.filterCourse(sort, category, keyword, page , size);

        return ApiResponse.<List<CourseCardResponse>>builder()
                .totalPages(course.getTotalPages())
                .result(course.stream().toList())
                .build();
    }



}
