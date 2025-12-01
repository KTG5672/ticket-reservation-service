package io.github.ktg.ticketing.domain.reservation.exception;

import io.github.ktg.ticketing.common.exception.BusinessException;
import io.github.ktg.ticketing.common.exception.ErrorCode;

public class ReservePerUserLimitException extends BusinessException {

    public ReservePerUserLimitException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ReservePerUserLimitException(ErrorCode errorCode, String additionalMessage) {
        super(errorCode, additionalMessage);
    }

}
