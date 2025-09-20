package io.github.ktg.ticketing.common.exception;

import lombok.AllArgsConstructor;

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
