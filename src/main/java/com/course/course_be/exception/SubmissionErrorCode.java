package com.course.course_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SubmissionErrorCode implements ErrorCode{
    SAVE_SUBMISSION_FAIL("SUBMISSION_1","Save submission is fail", HttpStatus.INTERNAL_SERVER_ERROR),
    SUBMISSION_EXIST("SUBMISSION_2","Submission exist", HttpStatus.BAD_REQUEST),
    SUBMISSION_INVALID("SUBMISSION_3","Submission invalid", HttpStatus.BAD_REQUEST),

    ;

    SubmissionErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
