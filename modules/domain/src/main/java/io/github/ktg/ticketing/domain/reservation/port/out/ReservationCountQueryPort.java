package io.github.ktg.ticketing.domain.reservation.port.out;

public interface ReservationCountQueryPort {

    int countReservedSeats(String userId, Long scheduleId);

}
