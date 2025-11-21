package io.github.ktg.ticketing.domain.reservation.port.in;

/**
 * 좌석 예약 결과
 * @param reservationId 생성된 예약 ID
 */
public record ReserveSeatResult(Long reservationId) {
}
