package com.pori.global.response;

import com.pori.global.exception.errorcode.ErrorCode;

import java.util.Collections;

public record ApiResponse<T>(
        boolean isSuccess,
        String code,
        String message,
        T results
) {
    private static final String SUCCESS_CODE = "REQUEST_OK";
    private static final String SUCCESS_MESSAGE = "요청이 성공했습니다.";
    private static final String CREATED_CODE = "REQUEST_CREATED";
    private static final String CREATED_MESSAGE = "리소스가 생성되었습니다.";

    public static ApiResponse<Object> empty() {
        return new ApiResponse<>(true, SUCCESS_CODE, SUCCESS_MESSAGE, Collections.emptyMap());
    }

    public static <T> ApiResponse<T> ok(T results) {
        return new ApiResponse<>(true, SUCCESS_CODE, SUCCESS_MESSAGE, results);
    }

    public static <T> ApiResponse<T> created(T results) {
        return new ApiResponse<>(true, CREATED_CODE, CREATED_MESSAGE, results);
    }

    public static ApiResponse<Object> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, T results) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), results);
    }
}
