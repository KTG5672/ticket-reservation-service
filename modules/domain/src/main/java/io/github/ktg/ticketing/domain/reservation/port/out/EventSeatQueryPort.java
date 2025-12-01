package io.github.ktg.ticketing.domain.reservation.port.out;

import io.github.ktg.ticketing.domain.reservation.dto.SeatSnapshot;
import java.util.List;

public interface EventSeatQueryPort {

    List<SeatSnapshot> findBySeatIds(List<Long> seatIds);

}
