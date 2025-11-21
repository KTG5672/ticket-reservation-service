package io.github.ktg.ticketing.domain.reservation.port.in;

import java.util.List;

/**
 * 좌석 예약 입력
 * @param userId 유저 식별자
 * @param eventSeatId 예약할 이벤트 좌석 식별자 list
 */
public record ReserveSeatCommand(String userId, List<Long> eventSeatId) {

}
