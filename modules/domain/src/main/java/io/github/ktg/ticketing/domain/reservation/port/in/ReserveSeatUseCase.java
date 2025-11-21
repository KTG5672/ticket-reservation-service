package io.github.ktg.ticketing.domain.reservation.port.in;

/**
 * 좌석 예약 유스케이스.
 * - 결제 대기 예약 도메인 생성
 */
public interface ReserveSeatUseCase {

    /**
     * 여러 좌석을 한 번에 예약(결제 대기)한다.
     * @param command 좌석 예약 입력
     * @return 생성된 예약 식별자
     */
    ReserveSeatResult reserveSeats(ReserveSeatCommand command);
}

