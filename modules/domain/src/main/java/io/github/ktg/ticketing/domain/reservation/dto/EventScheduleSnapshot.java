package io.github.ktg.ticketing.domain.reservation.dto;

import java.time.LocalDateTime;

public record EventScheduleSnapshot(Long id, LocalDateTime ticketOpenAt, LocalDateTime ticketCloseAt, int userBookingLimit) {

}
