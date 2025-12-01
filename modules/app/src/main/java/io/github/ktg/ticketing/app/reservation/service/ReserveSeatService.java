package io.github.ktg.ticketing.app.reservation.service;

import io.github.ktg.ticketing.domain.reservation.dto.EventScheduleSnapshot;
import io.github.ktg.ticketing.domain.reservation.dto.SeatSnapshot;
import io.github.ktg.ticketing.domain.reservation.exception.ReservationErrorCode;
import io.github.ktg.ticketing.domain.reservation.exception.SeatNotReservableException;
import io.github.ktg.ticketing.domain.reservation.exception.TicketSalePeriodException;
import io.github.ktg.ticketing.domain.reservation.model.Reservation;
import io.github.ktg.ticketing.domain.reservation.port.in.ReserveSeatCommand;
import io.github.ktg.ticketing.domain.reservation.port.in.ReserveSeatResult;
import io.github.ktg.ticketing.domain.reservation.port.in.ReserveSeatUseCase;
import io.github.ktg.ticketing.domain.reservation.port.out.EventScheduleQueryPort;
import io.github.ktg.ticketing.domain.reservation.port.out.EventSeatQueryPort;
import io.github.ktg.ticketing.domain.reservation.port.out.ReservationRepository;
import io.github.ktg.ticketing.domain.reservation.port.out.WaitingPaymentReservationStorePort;
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
 */
@Service
@RequiredArgsConstructor
public class ReserveSeatService implements ReserveSeatUseCase {

    public static final long WAITING_PAYMENT_EXPIRATION_MINUTES = 5L;
    private final ReservationRepository reservationRepository;
    private final EventSeatQueryPort eventSeatQueryPort;
    private final EventScheduleQueryPort eventScheduleQueryPort;
    private final WaitingPaymentReservationStorePort waitingPaymentReservationStorePort;


    /**
     * 좌석 예약
     * - 일정 조회 후 티켓 오픈 전, 마감 후면 예약 불가 (TicketSalePeriodException)
     * - 좌석 조회 후 예약 불가일 때 예외 (SeatNotReservableException)
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

        Reservation waitingPayment = Reservation.createWaitingPayment(userId, seats);
        Reservation saved = reservationRepository.save(waitingPayment);

        Long reservationId = saved.getId();
        waitingPaymentReservationStorePort.store(reservationId, WAITING_PAYMENT_EXPIRATION_MINUTES);
        return new ReserveSeatResult(reservationId);
    }

    private void validateSeatCount(List<SeatSnapshot> seats, List<Long> eventSeatIds) {
        if (seats.size() != eventSeatIds.size()) {
            throw new SeatNotReservableException(ReservationErrorCode.SEAT_NOT_FOUND);
        }
    }

    private void validateTicketOpenClose(LocalDateTime ticketOpenAt, LocalDateTime ticketCloseAt) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(ticketOpenAt)) {
            throw new TicketSalePeriodException(ReservationErrorCode.TICKET_NOT_OPEN);
        }
        if (now.isAfter(ticketCloseAt)) {
            throw new TicketSalePeriodException(ReservationErrorCode.TICKET_SALE_CLOSED);
        }
    }

    private void validateSeatAvailable(List<SeatSnapshot> seats) {
        seats.forEach(seat -> {
            if (!seat.available()) {
                throw new SeatNotReservableException(ReservationErrorCode.SEAT_NOT_RESERVABLE);
            }
        });
    }
}
