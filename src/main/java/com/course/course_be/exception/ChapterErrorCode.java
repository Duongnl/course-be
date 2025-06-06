package com.course.course_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChapterErrorCode implements ErrorCode{
    CHAPTER_NOT_FOUND  ("CHAPTER_1","Chapter not found", HttpStatus.NOT_FOUND),
    ;

    ChapterErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;

}
