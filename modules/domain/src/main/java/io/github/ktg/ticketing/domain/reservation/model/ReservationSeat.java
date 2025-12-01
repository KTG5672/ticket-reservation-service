package io.github.ktg.ticketing.domain.reservation.model;

import lombok.Getter;

@Getter
public class ReservationSeat {

    private final Long id;
    private final Reservation reservation;
    private final Long seatId;
    private final int price;

    private ReservationSeat(Long id, Reservation reservation, Long seatId, int price) {
        this.id = id;
        this.reservation = reservation;
        this.seatId = seatId;
        this.price = price;
    }

    public static ReservationSeat create(Reservation reservation, Long seatId, int price) {
        return new ReservationSeat(null, reservation, seatId, price);
    }

}
