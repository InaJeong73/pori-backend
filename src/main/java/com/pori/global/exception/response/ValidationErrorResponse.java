package com.pori.global.exception.response;

import java.util.List;

public record ValidationErrorResponse(
        List<ValidationError> validationErrors
) {
    public record ValidationError(
            String field,
            String message
    ) {
    }
}
