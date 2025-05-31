package com.course.course_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ProfileErrorCode implements ErrorCode {
    PROFILE_NOT_FOUND  ("PROFILE_1","Profile not found", HttpStatus.UNAUTHORIZED),
    ;

    ProfileErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
