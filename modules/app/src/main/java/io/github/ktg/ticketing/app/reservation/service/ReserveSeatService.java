package io.github.ktg.ticketing.app.reservation.service;

import io.github.ktg.ticketing.domain.reservation.dto.EventScheduleSnapshot;
import io.github.ktg.ticketing.domain.reservation.dto.SeatSnapshot;
import io.github.ktg.ticketing.domain.reservation.exception.ReservationErrorCode;
import io.github.ktg.ticketing.domain.reservation.exception.ReservePerUserLimitException;
import io.github.ktg.ticketing.domain.reservation.exception.SeatNotReservableException;
import io.github.ktg.ticketing.domain.reservation.exception.TicketSalePeriodException;
import io.github.ktg.ticketing.domain.reservation.model.Reservation;
import io.github.ktg.ticketing.domain.reservation.port.in.ReserveSeatCommand;
import io.github.ktg.ticketing.domain.reservation.port.in.ReserveSeatResult;
import io.github.ktg.ticketing.domain.reservation.port.in.ReserveSeatUseCase;
import io.github.ktg.ticketing.domain.reservation.port.out.EventScheduleQueryPort;
import io.github.ktg.ticketing.domain.reservation.port.out.EventSeatQueryPort;
import io.github.ktg.ticketing.domain.reservation.port.out.ReservationCountQueryPort;
import io.github.ktg.ticketing.domain.reservation.port.out.ReservationRepository;
import io.github.ktg.ticketing.domain.reservation.port.out.WaitingPaymentReservationStorePort;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 좌석 예약 서비스
 * - 좌석 예약 유스케이스 구현 (ReserveSeatUseCase)
 * - 이벤트 일정, 좌석 조회 port 의존 (EventSeatQueryPort, EventScheduleQueryPort)
 * - 결제 대기 상태 관리를 위한 저장소 port 의존 (WaitingPaymentReservationStorePort)
 * - 예약 count 조회 port 의존 (ReservationCountQueryPort)
 * - 테스트, 디버깅을 위한 시간 클래스 의존 (Clock)
 */
@Service
@RequiredArgsConstructor
public class ReserveSeatService implements ReserveSeatUseCase {

    public static final long WAITING_PAYMENT_EXPIRATION_MINUTES = 5L;
    private final ReservationRepository reservationRepository;
    private final EventSeatQueryPort eventSeatQueryPort;
    private final EventScheduleQueryPort eventScheduleQueryPort;
    private final WaitingPaymentReservationStorePort waitingPaymentReservationStorePort;
    private final ReservationCountQueryPort reservationCountQueryPort;
    private final Clock clock;

    /**
     * 좌석 예약
     * - 일정 조회 후 티켓 오픈 전, 마감 후면 예약 불가 (TicketSalePeriodException)
     * - 좌석 조회 후 예약 불가일 때 예외 (SeatNotReservableException)
     * - 사용자 당 최대 예약 수 < 예약 요청 좌석 개수 + 예약된 좌석 개수면 예외 (ReservePerUserLimitException)
     * - 결제 대기 상태로 저장
     * - 결제 대기 상태 관리를 위한 저장소에 저장 (만료시간 5분)
     * @param command 좌석 예약 입력
     * @return ReserveSeatResult 좌석 예약 결과
     */
    @Transactional
    @Override
    public ReserveSeatResult reserveSeats(ReserveSeatCommand command) {
        String userId = command.userId();
        List<Long> eventSeatIds = command.eventSeatIds();
        Long scheduleId = command.eventScheduleId();

        EventScheduleSnapshot schedule = eventScheduleQueryPort.findById(scheduleId);
        validateTicketOpenClose(schedule.ticketOpenAt(), schedule.ticketCloseAt());

        List<SeatSnapshot> seats = eventSeatQueryPort.findBySeatIds(eventSeatIds);
        validateSeatCount(seats, eventSeatIds);
        validateSeatAvailable(seats);
        validateUserReserveLimit(userId, scheduleId, seats.size(), schedule.maxReservePerUser());

        Reservation waitingPayment = Reservation.createWaitingPayment(userId, seats);
        Reservation saved = reservationRepository.save(waitingPayment);

        Long reservationId = saved.getId();
        waitingPaymentReservationStorePort.store(reservationId,
            Duration.ofMinutes(WAITING_PAYMENT_EXPIRATION_MINUTES));
        return new ReserveSeatResult(reservationId);
    }

    private void validateTicketOpenClose(LocalDateTime ticketOpenAt, LocalDateTime ticketCloseAt) {
        LocalDateTime now = LocalDateTime.now(clock);
        if (now.isBefore(ticketOpenAt)) {
            throw new TicketSalePeriodException(ReservationErrorCode.TICKET_NOT_OPEN);
        }
        if (now.isAfter(ticketCloseAt)) {
            throw new TicketSalePeriodException(ReservationErrorCode.TICKET_SALE_CLOSED);
        }
    }

    private void validateSeatCount(List<SeatSnapshot> seats, List<Long> eventSeatIds) {
        if (seats.size() != eventSeatIds.size()) {
            throw new SeatNotReservableException(ReservationErrorCode.SEAT_NOT_FOUND);
        }
    }

    private void validateSeatAvailable(List<SeatSnapshot> seats) {
        seats.forEach(seat -> {
            if (!seat.available()) {
                throw new SeatNotReservableException(ReservationErrorCode.SEAT_NOT_RESERVABLE);
            }
        });
    }

    private void validateUserReserveLimit(String userId, Long scheduleId, int requestCount,
        int maxReservePerUser) {
        int reservedCount = reservationCountQueryPort.countReservedSeats(userId, scheduleId);
        int totalReservedSeats = reservedCount + requestCount;

        if (totalReservedSeats > maxReservePerUser) {
            String errorMessage = String.format("request: %d, reserved: %d, limit: %d", requestCount, reservedCount, maxReservePerUser);
            throw new ReservePerUserLimitException(ReservationErrorCode.RESERVE_PER_USER_LIMIT,
                errorMessage);
        }
    }
}
