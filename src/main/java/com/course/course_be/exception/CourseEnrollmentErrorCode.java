package com.course.course_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CourseEnrollmentErrorCode implements  ErrorCode  {
    COURSE_ENROLLMENT_NOT_FOUND("COURSE_ENROLLMENT_1","Not found", HttpStatus.NOT_FOUND),
    COURSE_ENROLLMENT_IS_EXIST("COURSE_ENROLLMENT_2", "Exit course enrollment", HttpStatus.BAD_REQUEST ),
    COURSE_ENROLLMENT_STATUS_INVALID("COURSE_ENROLLMENT_3","Invalid status", HttpStatus.BAD_REQUEST ),
    EXIST_ACTIVE("COURSE_ENROLLMENT_4","Exist active", HttpStatus.BAD_REQUEST ),
    EXIST_INACTIVE("COURSE_ENROLLMENT_5","Exist inactive", HttpStatus.BAD_REQUEST ),
    EXIST_PENDING("COURSE_ENROLLMENT_6","Exist pending", HttpStatus.BAD_REQUEST ),




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
