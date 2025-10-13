package io.github.ktg.ticketing.domain.user.model;

import io.github.ktg.ticketing.domain.user.exception.EmailNotValidException;
import io.github.ktg.ticketing.domain.user.exception.UserErrorCode;
import java.util.regex.Pattern;

/**
 * 이메일 값 타입
 * - 입력 값: trim + lowercase 정규화
 * - 단순 형식 검증
 *   -
 */
public record Email(String value) {

    private final static Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$");

    public Email {
        value = normalize(value);
        validate(value);
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toLowerCase();
    }

    private static void validate(String value) {
        validateNullOrEmpty(value);
        validatePattern(value);
    }

    private static void validateNullOrEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw new EmailNotValidException(UserErrorCode.EMAIL_NOT_VALID);
        }
    }

    private static void validatePattern(String value) {
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new EmailNotValidException(UserErrorCode.EMAIL_PATTERN_NOT_MATCHED);
        }
    }

}
