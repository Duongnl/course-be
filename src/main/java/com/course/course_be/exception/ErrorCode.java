package com.course.course_be.exception;

import org.springframework.http.HttpStatus;

// Tao inteface cho loi de dung chung
public interface ErrorCode {

    public String getCode();

    public String getMessage();

    public HttpStatus getHttpStatus();
}
