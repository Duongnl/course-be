package com.course.course_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// Khai cac loi ve authenticate
@Getter
public enum AuthErrorCode implements ErrorCode {
    UNAUTHENTICATED("AUTH_1","Unauthenticated", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("AUTH_2","Account locked", HttpStatus.UNAUTHORIZED),
    ;

    AuthErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
