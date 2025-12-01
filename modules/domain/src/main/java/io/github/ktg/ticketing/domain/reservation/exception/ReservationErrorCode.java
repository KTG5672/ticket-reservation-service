package io.github.ktg.ticketing.domain.reservation.exception;

import io.github.ktg.ticketing.common.exception.ErrorCode;

public enum ReservationErrorCode implements ErrorCode {
    NOT_ENOUGH_RESERVE_SEATS("예약 좌석은 1개 이상이어야 합니다."),
    INVALID_STATUS_FOR_EXPIRE("만료 가능한 상태가 아닙니다."),
    INVALID_STATUS_FOR_COMPLETE("완료 가능한 상태가 아닙니다."),
    INVALID_STATUS_FOR_CANCEL("취소 가능한 상태가 아닙니다."),
    SEAT_NOT_RESERVABLE("좌석이 예약 가능한 상태가 아닙니다."),
    SEAT_NOT_FOUND("유효하지 않은 좌석 입니다."),
    TICKET_NOT_OPEN("티켓 오픈 전 입니다."),
    TICKET_SALE_CLOSED("티켓 판매 마감 입니다.");

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
