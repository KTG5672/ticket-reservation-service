package io.github.ktg.ticketing.common.exception;

import lombok.AllArgsConstructor;

/**
 * 공통 ErrorCode 클래스
 * - 도메인 및 비지니스 규칙과 무관한 ErrorCode
 */
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    UNKNOWN_ERROR("Unknown server error");

    private final String message;

    @Override
    public String code() {
        return this.name();
    }

    @Override
    public String message() {
        return message;
    }
}
