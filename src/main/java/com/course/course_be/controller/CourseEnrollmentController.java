package com.course.course_be.controller;

import com.course.course_be.dto.request.course_enrollment.ChangeStatusRequest;
import com.course.course_be.dto.request.course_enrollment.CourseEnrollmentAdminCreateRequest;
import com.course.course_be.dto.request.course_enrollment.CourseEnrollmentAdminFilterRequest;
import com.course.course_be.dto.request.course_enrollment.RegisterCourseClient;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.course.CourseEnrollmentAdminResponse;
import com.course.course_be.dto.response.course_enrollment.CourseEnrollmentResponse;
import com.course.course_be.dto.response.homeclient.CourseEnrollCardResponse;
import com.course.course_be.service.CourseEnrollmentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course-enrollment")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseEnrollmentController {
    CourseEnrollmentService courseEnrollmentService;

    @GetMapping("/course-enroll")
    public ApiResponse<List<CourseEnrollCardResponse>> getCourseStudying ()
    {
        return ApiResponse.<List<CourseEnrollCardResponse>>builder()
                .result(courseEnrollmentService.getCourseEnroll())
                .build();
    }

    @GetMapping("/list-admin")
    public ApiResponse<List<CourseEnrollmentAdminResponse>> getCourseEnrollmentsAdmin(@ModelAttribute CourseEnrollmentAdminFilterRequest request) {
        Page<CourseEnrollmentAdminResponse> courseEnrollmentPage = courseEnrollmentService.getListCourseEnrollment(request);
        return ApiResponse.<List<CourseEnrollmentAdminResponse>>builder()
                .result(courseEnrollmentPage.getContent())
                .totalItems(courseEnrollmentPage.getTotalElements())
                .currentPage(courseEnrollmentPage.getNumber())
                .totalPages(courseEnrollmentPage.getTotalPages())
                .build();
    }

    @PutMapping("/change-status-admin")
    public ApiResponse<CourseEnrollmentAdminResponse> changeStatus(@RequestBody ChangeStatusRequest changeStatusRequest) {
        return ApiResponse.<CourseEnrollmentAdminResponse>builder()
                .result(courseEnrollmentService.changeStatus(changeStatusRequest))
                .build();
    }

    @GetMapping("/of-account/{courseId}")
    public ApiResponse<CourseEnrollmentResponse> getCourseEnrollment(@PathVariable String courseId) {
        return ApiResponse.<CourseEnrollmentResponse>builder().result(courseEnrollmentService.getCourseEnrollmentRegister(courseId)).build();
    }

    @PostMapping("/register-course-client")
    public ApiResponse<CourseEnrollmentResponse> registerCourseClient(@Valid  @RequestBody RegisterCourseClient request) {
        return ApiResponse.<CourseEnrollmentResponse>builder()
                .result(courseEnrollmentService.registerCourse(request))
                .build();
    }

    @DeleteMapping("/cancel-course-register/{courseId}")
    public ApiResponse<String> cancelCourseRegister(@PathVariable String courseId) {
        courseEnrollmentService.cancelRegisterCourse(courseId);
        return ApiResponse.<String>builder().result("success").build();
    }

    @PostMapping("/create")
    public ApiResponse<CourseEnrollmentAdminResponse> createCourseEnrollment(@RequestBody CourseEnrollmentAdminCreateRequest request){
        return ApiResponse.<CourseEnrollmentAdminResponse>builder()
                .result(courseEnrollmentService.create(request))
                .build();
    }
}
