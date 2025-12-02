package io.github.ktg.ticketing.domain.reservation.model;

import lombok.Getter;

/**
 * 예약 좌석 도메인
 * - 정적 팩토리 메서드 제공 (create, reconstruct)
 */
@Getter
public class ReservationSeat {

    private final Long id;
    private final Long seatId;
    private final int price;

    private ReservationSeat(Long id, Long seatId, int price) {
        this.id = id;
        this.seatId = seatId;
        this.price = price;
    }

    /**
     * 초기 예약 좌석 생성 (without ID)
     * @param seatId 좌석 식별자
     * @param price 좌석 가격
     * @return ReservationSeat
     */
    public static ReservationSeat create(Long seatId, int price) {
        return new ReservationSeat(null, seatId, price);
    }

    /**
     * 예약 좌석 재구성 정적 메서드 (with ID)
     * @param id 예약 좌석 식별자
     * @param seatId 좌석 식별자
     * @param price 좌석 가격
     * @return ReservationSeat
     */
    public static ReservationSeat reconstruct(Long id, Long seatId, int price) {
        return new ReservationSeat(id, seatId, price);
    }

}
