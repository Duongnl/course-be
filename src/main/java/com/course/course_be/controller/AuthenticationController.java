package com.course.course_be.controller;

import com.course.course_be.dto.request.auth.AuthenticationRequest;
import com.course.course_be.dto.request.auth.RefreshTokenRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.auth.AuthenticationResponse;
import com.course.course_be.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;


//    Xin access token moi tu refresh token
    @PostMapping("refresh-token")
    public ApiResponse<AuthenticationResponse> refreshToken (@RequestBody @Valid RefreshTokenRequest authenticationRequest) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.refreshToken(authenticationRequest))
                .build();
    }

    @GetMapping("/hello")
    public  ApiResponse<String> hello() {
        return ApiResponse.<String>builder()
                .result("Hello")
                .build();
    }


//  Dang nhap bang google
    @PostMapping("/google")
    public ApiResponse<AuthenticationResponse> loginWithGoogle(@RequestBody AuthenticationRequest authenticationRequest) {
        System.out.println("authenticationRequest >>> " + authenticationRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.loginWithGoogle(authenticationRequest))
                .build();
    }

    @PostMapping("/introspect-refresh-token")
    public ApiResponse<AuthenticationResponse> introspectRefreshToken(@RequestBody RefreshTokenRequest request) {
        boolean bool = authenticationService.introspectRefreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(
                        AuthenticationResponse.builder()
                                .authenticated(bool)
                                .build()
                )
                .build();
    }


}
