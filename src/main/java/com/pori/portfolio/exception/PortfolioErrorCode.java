package com.pori.portfolio.exception;

import com.pori.global.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PortfolioErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "PORTFOLIO_001", "포트폴리오를 찾을 수 없습니다."),
    NOT_PUBLIC(HttpStatus.FORBIDDEN, "PORTFOLIO_002", "공개되지 않은 포트폴리오입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
