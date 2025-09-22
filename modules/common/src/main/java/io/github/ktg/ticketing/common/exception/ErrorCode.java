package io.github.ktg.ticketing.common.exception;

/**
 * ErrorCode 인터페이스
 * - code, message 메서드 제공
 * - 각 비지니스별 에러 코드는 해당 인터페이스를 Enum 으로 구현
 */
public interface ErrorCode {

    String code();
    String message();
}
