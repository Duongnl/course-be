package com.course.course_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AccountErrorCode  implements ErrorCode{
    ACCOUNT_NOT_FOUND  ("ACCOUNT_1","Account not found", HttpStatus.UNAUTHORIZED),
    SAVE_USER_FAIL("ACCOUNT_2","Save user is fail", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("ACCOUNT_3", "Email already exists", HttpStatus.BAD_REQUEST),
    USERNAME_ALREADY_EXISTS("ACCOUNT_4", "Username already exists", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_INCORRECT("ACCOUNT_4","Old Password Incorrect", HttpStatus.BAD_REQUEST ),
    NEW_PASSWORD_SAME_AS_OLD("ACCOUNT_5","New password same as old password", HttpStatus.BAD_REQUEST ),
    SAVE_PASSWORD_FAIL("ACCOUNT_6", "Failed to save password", HttpStatus.INTERNAL_SERVER_ERROR),
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
