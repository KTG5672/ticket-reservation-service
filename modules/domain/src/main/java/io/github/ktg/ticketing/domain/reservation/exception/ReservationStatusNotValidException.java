package io.github.ktg.ticketing.domain.reservation.exception;

import io.github.ktg.ticketing.common.exception.BusinessException;
import io.github.ktg.ticketing.common.exception.ErrorCode;

public class ReservationStatusNotValidException extends BusinessException {

    public ReservationStatusNotValidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
