package io.github.ktg.ticketing.common.exception;

import lombok.Getter;

/**
 * 비지니스 예외 최상위 클래스
 * - 모든 비지니스 예외는 해당 클래스를 상속
 * - ErrorCode 인터페이스를 가짐
 */
@Getter
public class BusinessException extends RuntimeException{

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

}
