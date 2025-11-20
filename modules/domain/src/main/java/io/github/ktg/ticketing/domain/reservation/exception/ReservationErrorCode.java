package io.github.ktg.ticketing.domain.reservation.exception;

import io.github.ktg.ticketing.common.exception.ErrorCode;

public enum ReservationErrorCode implements ErrorCode {
    NOT_ENOUGH_RESERVE_SEATS("예약 좌석은 1개 이상이어야 합니다."),
    INVALID_STATUS_FOR_EXPIRE("만료 가능한 상태가 아닙니다."),
    INVALID_STATUS_FOR_COMPLETE("완료 가능한 상태가 아닙니다."),
    INVALID_STATUS_FOR_CANCEL("취소 가능한 상태가 아닙니다.");

    private final String message;

    ReservationErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String code() {
        return name();
    }

    @Override
    public String message() {
        return message;
    }
}
