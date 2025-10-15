package io.github.ktg.ticketing.domain.user.exception;

import io.github.ktg.ticketing.common.exception.BusinessException;
import io.github.ktg.ticketing.common.exception.ErrorCode;

public class EmailNotValidException extends BusinessException {

    public EmailNotValidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
