package com.course.course_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum LessonErrorCode implements ErrorCode {
    LESSON_NOT_FOUND  ("LESSON_1","Lesson not found", HttpStatus.NOT_FOUND),
    ;

    LessonErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
