package io.github.ktg.ticketing.domain.user.exception;

import io.github.ktg.ticketing.common.exception.ErrorCode;

/**
 * 유저 도메인 에러 코드
 */
public enum UserErrorCode implements ErrorCode {

    PASSWORD_NOT_VALID("유효하지 않은 패스워드 입니다."),
    PASSWORD_LENGTH_NOT_VALID("패스워드는 8-24글자 이어야 합니다."),
    PASSWORD_MISSING_LETTER("패스워드는 영문자를 포함하여야 합니다."),
    PASSWORD_MISSING_DIGIT("패스워드는 숫자를 포함하여야 합니다."),
    PASSWORD_CONTAINS_WHITESPACE("패스워드에 공백을 포함할 수 없습니다.");

    private final String message;

    UserErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String code() {
        return this.name();
    }

    @Override
    public String message() {
        return message;
    }
}
