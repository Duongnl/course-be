package com.course.course_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum InValidErrorCode implements ErrorCode {
    JSON_INVALID ("INVALID_1", "Invalid json", HttpStatus.BAD_REQUEST),
    NOT_BLANK ("INVALID_2", "Invalid blank", HttpStatus.BAD_REQUEST),
    NOT_NULL ("INVALID_3", "Invalid null", HttpStatus.BAD_REQUEST),
    ;

    InValidErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
