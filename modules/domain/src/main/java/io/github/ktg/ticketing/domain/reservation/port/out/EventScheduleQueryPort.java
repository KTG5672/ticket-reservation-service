package io.github.ktg.ticketing.domain.reservation.port.out;

import io.github.ktg.ticketing.domain.reservation.dto.EventScheduleSnapshot;

public interface EventScheduleQueryPort {

    EventScheduleSnapshot findById(Long scheduleId);

}
