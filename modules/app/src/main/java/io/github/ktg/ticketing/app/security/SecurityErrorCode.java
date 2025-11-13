package io.github.ktg.ticketing.app.security;

import io.github.ktg.ticketing.common.exception.ErrorCode;

public enum SecurityErrorCode implements ErrorCode {

    UNAUTHORIZED("인증이 필요 합니다."),
    FORBIDDEN("접근 권한이 없습니다.");

    private final String message;

    SecurityErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String code() {
        return this.name();
    }

    @Override
    public String message() {
        return this.message;
    }
}
