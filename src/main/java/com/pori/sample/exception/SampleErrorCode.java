package com.pori.sample.exception;

import com.pori.global.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

public enum SampleErrorCode implements ErrorCode {

    SAMPLE_NOT_FOUND(HttpStatus.NOT_FOUND, "SAMPLE404", "샘플 리소스를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    SampleErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
