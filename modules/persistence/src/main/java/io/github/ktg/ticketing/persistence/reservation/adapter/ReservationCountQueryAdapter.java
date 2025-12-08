package io.github.ktg.ticketing.persistence.reservation.adapter;

import io.github.ktg.ticketing.domain.reservation.port.out.ReservationCountQueryPort;
import io.github.ktg.ticketing.persistence.reservation.repository.ReservationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 예약 수 조회 port (ReservationCountQueryPort) 구현 Adapter
 */
@Component
@RequiredArgsConstructor
public class ReservationCountQueryAdapter implements ReservationCountQueryPort {

    private final ReservationQueryRepository reservationQueryRepository;

    @Override
    public int countReservedSeats(String userId, Long scheduleId) {
        return reservationQueryRepository.countReservedSeats(userId, scheduleId);
    }
}
