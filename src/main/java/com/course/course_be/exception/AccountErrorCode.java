package com.course.course_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AccountErrorCode  implements ErrorCode{
    ACCOUNT_NOT_FOUND  ("ACCOUNT_1","Account not found", HttpStatus.UNAUTHORIZED),
    SAVE_USER_FAIL("ACCOUNT_2","Save user is fail", HttpStatus.BAD_REQUEST),
    ;

    AccountErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
