package com.course.course_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CourseEnrollmentErrorCode implements  ErrorCode  {
    COURSE_ENROLLMENT_NOT_FOUND("COURSE_ENROLLMENT_1","Course enrollment not found", HttpStatus.NOT_FOUND)
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
