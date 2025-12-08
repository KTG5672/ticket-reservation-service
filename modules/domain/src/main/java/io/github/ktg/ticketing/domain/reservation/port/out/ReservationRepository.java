package io.github.ktg.ticketing.domain.reservation.port.out;

import io.github.ktg.ticketing.domain.reservation.model.Reservation;
import java.util.Optional;

/**
 * 예약 도메인 영속성 Port
 */
public interface ReservationRepository {

    Optional<Reservation> findById(Long reservationId);
    Reservation save(Reservation reservation);

}
