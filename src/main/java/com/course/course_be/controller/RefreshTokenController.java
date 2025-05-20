package com.course.course_be.controller;

import com.course.course_be.dto.request.auth.RefreshTokenRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refresh-token")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenController {

    RefreshTokenService refreshTokenService;

    @PostMapping("/delete-refresh-token")
    public ApiResponse<?> deleteRefreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        refreshTokenService.deleteRefreshToken(request);
        return ApiResponse.builder().build();
    }
}
