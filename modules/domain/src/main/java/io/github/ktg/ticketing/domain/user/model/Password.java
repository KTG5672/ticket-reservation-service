package io.github.ktg.ticketing.domain.user.model;

import io.github.ktg.ticketing.domain.user.exception.PasswordNotValidException;
import io.github.ktg.ticketing.domain.user.exception.UserErrorCode;

/**
 * 패스워드 값 타입
 * - 값은 null 또는 빈 값을허용 하지 않음
 * - 영문자 + 숫자 조합 필수
 * - 8-24 글자 제한
 * - 공백/개행 문자를 포함하지 않음
 */
public record Password(String value) {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 24;

    public Password {
        validate(value);
    }

    private static void validate(String password) {
        if (password == null || password.isEmpty()) {
            throw new PasswordNotValidException(UserErrorCode.PASSWORD_NOT_VALID);
        }
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            throw new PasswordNotValidException(UserErrorCode.PASSWORD_LENGTH_NOT_VALID);
        }
        validateNoWhitespace(password);
        validateContainsLetterAndDigit(password);
    }

    private static void validateNoWhitespace(String password) {
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isWhitespace(c)) {
                throw new PasswordNotValidException(UserErrorCode.PASSWORD_CONTAINS_WHITESPACE);
            }
        }
    }

    private static void validateContainsLetterAndDigit(String password) {
        boolean containsLetter = false;
        boolean containsDigit = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (isLetter(c)) {
                containsLetter = true;
            }
            if (isDigit(c)) {
                containsDigit = true;
            }
            if (containsLetter && containsDigit) {
                break;
            }
        }
        if (!containsLetter) {
            throw new PasswordNotValidException(UserErrorCode.PASSWORD_MISSING_LETTER);
        }
        if (!containsDigit) {
            throw new PasswordNotValidException(UserErrorCode.PASSWORD_MISSING_DIGIT);
        }
    }

    private static boolean isLetter(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
    }

    private static boolean isDigit(char c) {
        return ('0' <= c && c <= '9');
    }

}
