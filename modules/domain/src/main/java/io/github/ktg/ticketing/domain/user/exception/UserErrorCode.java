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
    PASSWORD_CONTAINS_WHITESPACE("패스워드에 공백을 포함할 수 없습니다."),
    EMAIL_NOT_VALID("유효하지 않은 이메일 입니다."),
    EMAIL_PATTERN_NOT_MATCHED("이메일 형식이 올바르지 않습니다."),
    EMAIL_DUPLICATED("중복된 이메일 입니다."),
    EMAIL_NOT_FOUND("가입 되지 않은 이메일 입니다."),
    PASSWORD_NOT_MATCHED("비밀번호가 일치 하지 않습니다.");

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
