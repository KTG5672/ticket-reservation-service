package io.github.ktg.ticketing.domain.reservation.exception;

import io.github.ktg.ticketing.common.exception.BusinessException;
import io.github.ktg.ticketing.common.exception.ErrorCode;

public class ReservationNotValidException extends BusinessException {

    public ReservationNotValidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
