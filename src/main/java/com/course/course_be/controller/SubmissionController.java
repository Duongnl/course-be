package com.course.course_be.controller;

import com.course.course_be.dto.request.submissionadmin.GradedSubmissionRequest;
import com.course.course_be.dto.request.submissionclient.SubmissionClientRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.submissionadmin.SubmissionAdminResponse;
import com.course.course_be.dto.response.submissionclient.SubmissionClientResponse;
import com.course.course_be.entity.Submission;
import com.course.course_be.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/submission")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionController {
    SubmissionService submissionService;

    @PostMapping("")
    public ApiResponse<?> submission(@RequestBody @Valid SubmissionClientRequest request) {
        submissionService.submission(request);
        return ApiResponse.builder()
                .build();
    }

    @GetMapping("")
    public ApiResponse<List<SubmissionAdminResponse>> filterSubmissionAdmin(@RequestParam(required = false) Integer page,
                                                                       @RequestParam(required = false) Integer perPage,
                                                                       @RequestParam(required = false) String courseName,
                                                                       @RequestParam(required = false) String lessonName,
                                                                       @RequestParam(required = false) String submitterName,
                                                                       @RequestParam(required = false) String submitterUsername,
                                                                       @RequestParam(required = false) String status,
                                                                       @RequestParam(required = false) String from,
                                                                       @RequestParam(required = false) String to


    ) {
        Page<SubmissionAdminResponse> submissionAdminResponsePage = submissionService.filterSubmissionAdmin(page, perPage, courseName, lessonName, submitterName, submitterUsername, status, from, to);
        return ApiResponse.<List<SubmissionAdminResponse>>builder()
                .result(submissionAdminResponsePage.stream().toList())
                .totalPages(submissionAdminResponsePage.getTotalPages())
                .build();
    }


    @GetMapping("/client-filter")
    public ApiResponse<List<SubmissionClientResponse>> filterSubmissionClient(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer perPage,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String lessonName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ){
        Page<SubmissionClientResponse> submissionPage = submissionService.filterSubmissionClient(page,perPage, courseName, lessonName, status, from, to);
        return ApiResponse.<List<SubmissionClientResponse>>builder()
                .result(submissionPage.stream().toList())
                .totalPages(submissionPage.getTotalPages())
                .build();
    }


    @DeleteMapping("/{id}")
    public ApiResponse<?> deletedSubmission(@PathVariable String id) {
        submissionService.deletedSubmission(id);
        return ApiResponse.builder()
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateSubmission(@PathVariable String id, @RequestBody @Valid GradedSubmissionRequest request) {
        submissionService.updateSubmission(id, request);
        return ApiResponse.builder()
                .build();
    }


}
