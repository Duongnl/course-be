package com.course.course_be.controller;

import com.course.course_be.dto.request.course.CreateCourseRequest;
import com.course.course_be.dto.request.course.UpdateCourseRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.category.CategoryResponse;
import com.course.course_be.dto.response.course.CourseResponse;
import com.course.course_be.dto.response.homeclient.CourseCardResponse;
import com.course.course_be.service.CourseService;
import com.course.course_be.utils.PageableUtil;
import jakarta.validation.Valid;
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

    @GetMapping
    public ApiResponse<List<CourseResponse>> getAllCourses(@RequestParam(required = false) Integer page,
                                                           @RequestParam(required = false) Integer perPage,
                                                           @RequestParam(required = false) String sort,
                                                           @RequestParam(required = false) String name,
                                                           @RequestParam(required = false) String detail,
                                                           @RequestParam(required = false) String status) {
        Pageable pageable = PageableUtil.createPageable(page, perPage, sort);
        Page<CourseResponse> pageResult = courseService.getAll(name, detail, status, pageable);
        return ApiResponse.<List<CourseResponse>>builder()
                .result(pageResult.stream().toList())
                .currentPage(page != null ? page : 1)
                .totalPages(pageResult.getTotalPages())
                .totalItems(pageResult.getTotalElements()).build();
    }


    @GetMapping("/filter")
    public ApiResponse<List<CourseCardResponse>> filterCourses(@RequestParam(required = false) String sort,
                                                               @RequestParam(required = false) String category,
                                                               @RequestParam(required = false) String keyword,
                                                               @RequestParam int page,
                                                               @RequestParam int size
    ) {

        Page<CourseCardResponse> course = courseService.filterCourse(sort, category, keyword, page, size);

        return ApiResponse.<List<CourseCardResponse>>builder()
                .totalPages(course.getTotalPages())
                .result(course.stream().toList())
                .build();
    }

    @PostMapping
    public ApiResponse<CourseResponse> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        return ApiResponse.<CourseResponse>builder().result(
                courseService.createNew(request)
        ).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CourseResponse> updateCourse(@PathVariable String id, @Valid @RequestBody UpdateCourseRequest request) {
        return ApiResponse.<CourseResponse>builder().result(
                courseService.update(id, request)
        ).build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCourse(@PathVariable String id) {
        courseService.delete(id);
        return ApiResponse.<String>builder()
                .message("Course deleted successfully")
                .build();
    }

}
