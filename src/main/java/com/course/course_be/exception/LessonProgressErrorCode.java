package com.course.course_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum LessonProgressErrorCode implements ErrorCode {
    LESSON_PROGRESS_NOT_FOUND  ("LESSON_PROGRESS_1","Lesson progress not found", HttpStatus.NOT_FOUND),
    LESSON_PROGRESS_FORBIDDEN  ("LESSON_PROGRESS_2","You are not enrolled in this course.", HttpStatus.FORBIDDEN)
    ;

    LessonProgressErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
