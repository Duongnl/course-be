package com.course.course_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum CategoryErrorCode implements ErrorCode {
    CATEGORY_NOT_FOUND  ("CATEGORY_1","Category not found", HttpStatus.NOT_FOUND)
    ;

    CategoryErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
