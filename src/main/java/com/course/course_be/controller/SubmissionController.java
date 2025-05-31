package com.course.course_be.controller;

import com.course.course_be.dto.request.submissionclient.SubmissionClientRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/submission")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionController {
    SubmissionService submissionService;

    @PostMapping("")
    public ApiResponse<?> submission (@RequestBody @Valid SubmissionClientRequest request)
    {
        submissionService.submission(request);
        return ApiResponse.builder()
                .build();
    }



}
