package com.course.course_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CourseEnrollmentErrorCode implements  ErrorCode  {

    ;

    CourseEnrollmentErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;


}
