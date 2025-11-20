package io.github.ktg.ticketing.domain.reservation.model;

import io.github.ktg.ticketing.domain.event.model.EventSeat;
import io.github.ktg.ticketing.domain.reservation.exception.ReservationErrorCode;
import io.github.ktg.ticketing.domain.reservation.exception.ReservationNotValidException;
import io.github.ktg.ticketing.domain.reservation.exception.ReservationStatusNotValidException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * 예약 도메인
 * - 예약 좌석 1개 이상이여야 하며 예약 생성 시에만 추가 가능
 * - 예약 상태 변경 기능 (예약 완료, 취소, 만료)
 */
@Getter
public class Reservation {

    private Long id;
    private final String userId;
    private ReservationStatus status;
    private final List<ReservationSeat> seats;

    private Reservation(Long id, String userId, ReservationStatus status,
        List<EventSeat> seats) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.seats = new ArrayList<>();
        if (seats == null || seats.isEmpty()) {
            throw new ReservationNotValidException(ReservationErrorCode.NOT_ENOUGH_RESERVE_SEATS);
        }
        for (EventSeat seat : seats) {
            addEventSeat(seat);
        }
    }

    /**
     * 결제 대기 상태 예약 생성
     *
     * @param userId 유저 식별자
     * @param seats  이벤트 좌석 List
     * @return Reservation 결제 대기 상태 예약
     */
    public static Reservation createWaitingPayment(String userId, List<EventSeat> seats) {
        return new Reservation(null, userId, ReservationStatus.WAITING_PAYMENT, seats);
    }

    private void addEventSeat(EventSeat seat) {
        seats.add(ReservationSeat.create(this, seat.getId(), seat.getPrice()));
    }

    /**
     * 예약 상태를 만료로 변경
     * - 만료 가능한 상태: WAITING_PAYMENT
     */
    public void expire() {
        if (!canExpire()) {
            throw new ReservationStatusNotValidException(
                ReservationErrorCode.INVALID_STATUS_FOR_EXPIRE);
        }
        status = ReservationStatus.EXPIRED;
    }

    private boolean canExpire() {
        return status == ReservationStatus.WAITING_PAYMENT;
    }

    /**
     * 예약 상태를 완료로 변경
     * - 완료 가능한 상태: WAITING_PAYMENT
     */
    public void complete() {
        if (!canComplete()) {
            throw new ReservationStatusNotValidException(
                ReservationErrorCode.INVALID_STATUS_FOR_COMPLETE);
        }
        status = ReservationStatus.COMPLETED;
    }

    private boolean canComplete() {
        return status == ReservationStatus.WAITING_PAYMENT;
    }

    /**
     * 예약 상태를 취소로 변경
     * - 취소 가능한 상태: WAITING_PAYMENT, COMPLETE
     */
    public void cancel() {
        if (!canCancel()) {
            throw new ReservationStatusNotValidException(
                ReservationErrorCode.INVALID_STATUS_FOR_CANCEL);
        }
        status = ReservationStatus.CANCELED;
    }

    private boolean canCancel() {
        return status == ReservationStatus.WAITING_PAYMENT
            || status == ReservationStatus.COMPLETED;
    }

}
