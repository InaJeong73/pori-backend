package com.propofol.global.exception.handler;

import com.propofol.global.exception.BusinessException;
import com.propofol.global.exception.errorcode.GlobalErrorCode;
import com.propofol.global.exception.response.ValidationErrorResponse;
import com.propofol.global.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException exception) {
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(ApiResponse.error(exception.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ValidationErrorResponse>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception
    ) {
        return handleValidationException(exception);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.status(GlobalErrorCode.BAD_REQUEST.getHttpStatus())
                .body(ApiResponse.error(GlobalErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception exception) {
        log.error("Unhandled exception", exception);
        return ResponseEntity.status(GlobalErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.error(GlobalErrorCode.INTERNAL_SERVER_ERROR));
    }

    private ResponseEntity<ApiResponse<ValidationErrorResponse>> handleValidationException(BindException exception) {
        var validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationErrorResponse.ValidationError(error.getField(), error.getDefaultMessage()))
                .toList();

        return ResponseEntity.status(GlobalErrorCode.BAD_REQUEST.getHttpStatus())
                .body(ApiResponse.error(GlobalErrorCode.BAD_REQUEST, new ValidationErrorResponse(validationErrors)));
    }
}
