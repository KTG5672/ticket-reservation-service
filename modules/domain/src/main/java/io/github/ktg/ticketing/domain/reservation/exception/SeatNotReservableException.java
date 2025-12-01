package io.github.ktg.ticketing.domain.reservation.exception;

import io.github.ktg.ticketing.common.exception.BusinessException;
import io.github.ktg.ticketing.common.exception.ErrorCode;

public class SeatNotReservableException extends BusinessException {

    public SeatNotReservableException(ErrorCode errorCode) {
        super(errorCode);
    }

}
