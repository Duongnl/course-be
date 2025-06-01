package com.course.course_be.controller;

import com.course.course_be.dto.request.profile.ProfileUpdateRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.profile.UserProfileResponse;
import com.course.course_be.service.ProfileService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {

    ProfileService profileService;

    @GetMapping("/current-profile")
    public ApiResponse<UserProfileResponse> getCurrentProfile() {
        return ApiResponse.<UserProfileResponse>builder()
                .result(profileService.getCurrentProfile())
                .build();
    }


    @PutMapping("/change-profile")
    public ApiResponse<UserProfileResponse> updateAccount(@Valid @RequestBody ProfileUpdateRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(profileService.updateProfile(request))
                .build();
    }
}
