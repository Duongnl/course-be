package com.course.course_be.exception;

// Chuyen cac loi logic vao global exception
public class AppException extends  RuntimeException{
    private ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        // goi contructer cua lop cha, lop cha truyen vao thong diep, appException.getMassage()
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
