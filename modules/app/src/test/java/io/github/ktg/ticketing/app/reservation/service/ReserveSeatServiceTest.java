package io.github.ktg.ticketing.app.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

import io.github.ktg.ticketing.domain.reservation.dto.EventScheduleSnapshot;
import io.github.ktg.ticketing.domain.reservation.dto.SeatSnapshot;
import io.github.ktg.ticketing.domain.reservation.exception.ReservationErrorCode;
import io.github.ktg.ticketing.domain.reservation.exception.ReservePerUserLimitException;
import io.github.ktg.ticketing.domain.reservation.exception.SeatNotReservableException;
import io.github.ktg.ticketing.domain.reservation.exception.TicketSalePeriodException;
import io.github.ktg.ticketing.domain.reservation.model.Reservation;
import io.github.ktg.ticketing.domain.reservation.model.ReservationStatus;
import io.github.ktg.ticketing.domain.reservation.port.in.ReserveSeatCommand;
import io.github.ktg.ticketing.domain.reservation.port.in.ReserveSeatResult;
import io.github.ktg.ticketing.domain.reservation.port.out.EventScheduleQueryPort;
import io.github.ktg.ticketing.domain.reservation.port.out.EventSeatQueryPort;
import io.github.ktg.ticketing.domain.reservation.port.out.ReservationCountQueryPort;
import io.github.ktg.ticketing.domain.reservation.port.out.ReservationRepository;
import io.github.ktg.ticketing.domain.reservation.port.out.WaitingPaymentReservationStorePort;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReserveSeatServiceTest {


    ReserveSeatService reserveSeatService;

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    EventSeatQueryPort eventSeatQueryPort;

    @Mock
    EventScheduleQueryPort eventScheduleQueryPort;

    @Mock
    WaitingPaymentReservationStorePort waitingPaymentReservationStorePort;

    @Mock
    ReservationCountQueryPort reservationCountQueryPort;

    @BeforeEach
    void setUp() {
        reserveSeatService = new ReserveSeatService(reservationRepository, eventSeatQueryPort,
            eventScheduleQueryPort, waitingPaymentReservationStorePort, reservationCountQueryPort,
            Clock.systemDefaultZone());
    }

    @Test
    @DisplayName("예약 시 티켓 오픈 시간 전이면 실패한다.")
    void 예약_시_티켓_오픈_시간_전이면_실패() {
        // given
        String userId = "user1234";
        Long eventScheduleId = 1L;
        List<Long> eventSeatIds = List.of(1L, 2L);
        ReserveSeatCommand command = new ReserveSeatCommand(userId, eventScheduleId, eventSeatIds);
        LocalDateTime ticketOpenAt = LocalDateTime.now().plusMinutes(5);
        LocalDateTime ticketCloseAt = ticketOpenAt.plusMinutes(5);

        when(eventScheduleQueryPort.findById(eventScheduleId)).thenReturn(new EventScheduleSnapshot(1L, ticketOpenAt, ticketCloseAt, 1));

        // when
        // then
        assertThatThrownBy(() -> reserveSeatService.reserveSeats(command))
            .isInstanceOf(TicketSalePeriodException.class)
            .extracting("errorCode").isEqualTo(ReservationErrorCode.TICKET_NOT_OPEN);
    }

    @Test
    @DisplayName("예약 시 티켓 마감 시간 후면 실패한다.")
    void 예약_시_티켓_마감_시간_후면_실패() {
        // given
        String userId = "user1234";
        Long eventScheduleId = 1L;
        List<Long> eventSeatIds = List.of(1L, 2L);
        ReserveSeatCommand command = new ReserveSeatCommand(userId, eventScheduleId, eventSeatIds);
        LocalDateTime ticketOpenAt = LocalDateTime.now().minusMinutes(5);
        LocalDateTime ticketCloseAt = ticketOpenAt.plusMinutes(3);

        when(eventScheduleQueryPort.findById(eventScheduleId)).thenReturn(new EventScheduleSnapshot(1L, ticketOpenAt, ticketCloseAt, 1));

        // when
        // then
        assertThatThrownBy(() -> reserveSeatService.reserveSeats(command))
            .isInstanceOf(TicketSalePeriodException.class)
            .extracting("errorCode").isEqualTo(ReservationErrorCode.TICKET_SALE_CLOSED);
    }

    @Test
    @DisplayName("예약 시 좌석 상태가 예약 불가 상태면 실패한다.")
    void 예약_시_좌석_상태가_예약_불가_상태면_실패() {
        // given
        String userId = "user1234";
        Long eventScheduleId = 1L;
        List<Long> eventSeatIds = List.of(1L, 2L);
        ReserveSeatCommand command = new ReserveSeatCommand(userId, eventScheduleId, eventSeatIds);
        LocalDateTime ticketOpenAt = LocalDateTime.now().minusMinutes(5);
        LocalDateTime ticketCloseAt = ticketOpenAt.plusMinutes(10);
        List<SeatSnapshot> seats = List.of(
            new SeatSnapshot(1L, 1000, true),
            new SeatSnapshot(2L, 1000, false));

        when(eventScheduleQueryPort.findById(eventScheduleId)).thenReturn(new EventScheduleSnapshot(1L, ticketOpenAt, ticketCloseAt, 1));
        when(eventSeatQueryPort.findBySeatIds(eventSeatIds)).thenReturn(seats);
        // when
        // then
        assertThatThrownBy(() -> reserveSeatService.reserveSeats(command))
            .isInstanceOf(SeatNotReservableException.class)
            .extracting("errorCode").isEqualTo(ReservationErrorCode.SEAT_NOT_RESERVABLE);
    }

    @Test
    @DisplayName("예약 시 요청 좌석 개수와 실제 좌석 개수가 다르면 실패한다.")
    void 예약_시_요청_좌석_개수와_실제_좌석_개수가_다르면_실패() {
        // given
        String userId = "user1234";
        Long eventScheduleId = 1L;
        List<Long> eventSeatIds = List.of(1L, 2L);
        ReserveSeatCommand command = new ReserveSeatCommand(userId, eventScheduleId, eventSeatIds);
        LocalDateTime ticketOpenAt = LocalDateTime.now().minusMinutes(5);
        LocalDateTime ticketCloseAt = ticketOpenAt.plusMinutes(10);
        List<SeatSnapshot> seats = List.of(
            new SeatSnapshot(1L, 1000, true));

        when(eventScheduleQueryPort.findById(eventScheduleId)).thenReturn(new EventScheduleSnapshot(1L, ticketOpenAt, ticketCloseAt, 1));
        when(eventSeatQueryPort.findBySeatIds(eventSeatIds)).thenReturn(seats);
        // when
        // then
        assertThatThrownBy(() -> reserveSeatService.reserveSeats(command))
            .isInstanceOf(SeatNotReservableException.class)
            .extracting("errorCode").isEqualTo(ReservationErrorCode.SEAT_NOT_FOUND);
    }

    @Test
    @DisplayName("예약 완료 후 예약 식별자를 리턴한다.")
    void 예약_완료_후_예약_식별자를_리턴한다() {
        // given
        String userId = "user1234";
        Long eventScheduleId = 1L;
        List<Long> eventSeatIds = List.of(1L, 2L);
        ReserveSeatCommand command = new ReserveSeatCommand(userId, eventScheduleId, eventSeatIds);
        LocalDateTime ticketOpenAt = LocalDateTime.now().minusMinutes(5);
        LocalDateTime ticketCloseAt = ticketOpenAt.plusMinutes(10);
        List<SeatSnapshot> seats = List.of(
            new SeatSnapshot(1L, 1000, true),
            new SeatSnapshot(2L, 1000, true));
        Reservation waitingPayment = Reservation.createWaitingPayment(userId, seats);
        Reservation withId = waitingPayment.withId(1L);

        when(eventScheduleQueryPort.findById(eventScheduleId)).thenReturn(new EventScheduleSnapshot(1L, ticketOpenAt, ticketCloseAt, 3));
        when(eventSeatQueryPort.findBySeatIds(eventSeatIds)).thenReturn(seats);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(withId);

        // when
        ReserveSeatResult reserveSeatResult = reserveSeatService.reserveSeats(command);
        // then
        assertThat(reserveSeatResult.reservationId()).isNotNull();
        assertThat(reserveSeatResult.reservationId()).isEqualTo(withId.getId());
    }

    @Test
    @DisplayName("예약 시 결제 대기 상태로 저장된다.")
    void 예약_시_결제_대기_상태로_저장된다() {
        // given
        String userId = "user1234";
        Long eventScheduleId = 1L;
        List<Long> eventSeatIds = List.of(1L, 2L);
        ReserveSeatCommand command = new ReserveSeatCommand(userId, eventScheduleId, eventSeatIds);
        LocalDateTime ticketOpenAt = LocalDateTime.now().minusMinutes(5);
        LocalDateTime ticketCloseAt = ticketOpenAt.plusMinutes(10);
        List<SeatSnapshot> seats = List.of(
            new SeatSnapshot(1L, 1000, true),
            new SeatSnapshot(2L, 1000, true));
        Reservation waitingPayment = Reservation.createWaitingPayment(userId, seats);

        when(eventScheduleQueryPort.findById(eventScheduleId)).thenReturn(new EventScheduleSnapshot(1L, ticketOpenAt, ticketCloseAt, 3));
        when(eventSeatQueryPort.findBySeatIds(eventSeatIds)).thenReturn(seats);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(waitingPayment);

        // when
        reserveSeatService.reserveSeats(command);
        // then
        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        then(reservationRepository).should().save(reservationCaptor.capture());
        Reservation saved = reservationCaptor.getValue();
        assertThat(saved.getStatus()).isEqualTo(ReservationStatus.WAITING_PAYMENT);
    }

    @Test
    @DisplayName("예약 시 결제 대기 상태 관리를 위한 저장소에 저장한다.(만료 시간 5분)")
    void 예약_시_결제_대기_상태_관리를_위한_저장소에_저장한다() {
        // given
        String userId = "user1234";
        Long eventScheduleId = 1L;
        List<Long> eventSeatIds = List.of(1L, 2L);
        ReserveSeatCommand command = new ReserveSeatCommand(userId, eventScheduleId, eventSeatIds);
        LocalDateTime ticketOpenAt = LocalDateTime.now().minusMinutes(5);
        LocalDateTime ticketCloseAt = ticketOpenAt.plusMinutes(10);
        List<SeatSnapshot> seats = List.of(
            new SeatSnapshot(1L, 1000, true),
            new SeatSnapshot(2L, 1000, true));
        long reservationId = 1L;
        Reservation waitingPayment = Reservation.createWaitingPayment(userId, seats);
        Reservation saved = waitingPayment.withId(reservationId);

        when(eventScheduleQueryPort.findById(eventScheduleId)).thenReturn(new EventScheduleSnapshot(1L, ticketOpenAt, ticketCloseAt, 3));
        when(eventSeatQueryPort.findBySeatIds(eventSeatIds)).thenReturn(seats);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        // when
        reserveSeatService.reserveSeats(command);

        // then
        then(waitingPaymentReservationStorePort).should().store(reservationId, Duration.ofMinutes(ReserveSeatService.WAITING_PAYMENT_EXPIRATION_MINUTES));
    }

    @Test
    @DisplayName("예약 시 티켓 오픈 시간 정각이면 성공한다.")
    void 예약_시_티켓_오픈_시간_정각이면_성공() {
        // given
        String userId = "user1234";
        Long eventScheduleId = 1L;
        List<Long> eventSeatIds = List.of(1L, 2L);
        ReserveSeatCommand command = new ReserveSeatCommand(userId, eventScheduleId, eventSeatIds);
        Clock fixedTime = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        LocalDateTime ticketOpenAt = LocalDateTime.now(fixedTime);
        LocalDateTime ticketCloseAt = ticketOpenAt.plusMinutes(5);
        reserveSeatService = new ReserveSeatService(reservationRepository, eventSeatQueryPort,
            eventScheduleQueryPort, waitingPaymentReservationStorePort, reservationCountQueryPort, fixedTime);
        List<SeatSnapshot> seats = List.of(
            new SeatSnapshot(1L, 1000, true),
            new SeatSnapshot(2L, 1000, true));
        long reservationId = 1L;
        Reservation waitingPayment = Reservation.createWaitingPayment(userId, seats);
        Reservation saved = waitingPayment.withId(reservationId);

        when(eventScheduleQueryPort.findById(eventScheduleId)).thenReturn(new EventScheduleSnapshot(1L, ticketOpenAt, ticketCloseAt, 3));
        when(eventSeatQueryPort.findBySeatIds(eventSeatIds)).thenReturn(seats);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        // when
        ReserveSeatResult result = reserveSeatService.reserveSeats(command);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("예약 시 티켓 마감 시간 정각이면 성공한다.")
    void 예약_시_티켓_마감_시간_정각이면_성공() {
        // given
        String userId = "user1234";
        Long eventScheduleId = 1L;
        List<Long> eventSeatIds = List.of(1L, 2L);
        ReserveSeatCommand command = new ReserveSeatCommand(userId, eventScheduleId, eventSeatIds);
        Clock fixedTime = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        LocalDateTime ticketOpenAt = LocalDateTime.now(fixedTime).minusMinutes(5);
        LocalDateTime ticketCloseAt = LocalDateTime.now(fixedTime);
        reserveSeatService = new ReserveSeatService(reservationRepository, eventSeatQueryPort,
            eventScheduleQueryPort, waitingPaymentReservationStorePort, reservationCountQueryPort, fixedTime);
        List<SeatSnapshot> seats = List.of(
            new SeatSnapshot(1L, 1000, true),
            new SeatSnapshot(2L, 1000, true));
        long reservationId = 1L;
        Reservation waitingPayment = Reservation.createWaitingPayment(userId, seats);
        Reservation saved = waitingPayment.withId(reservationId);

        when(eventScheduleQueryPort.findById(eventScheduleId)).thenReturn(new EventScheduleSnapshot(1L, ticketOpenAt, ticketCloseAt, 3));
        when(eventSeatQueryPort.findBySeatIds(eventSeatIds)).thenReturn(seats);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        // when
        ReserveSeatResult result = reserveSeatService.reserveSeats(command);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("")
    void 예약할_좌석과_예약된_좌석의_합이_한_유저당_최대_예매_개수보다_초과면_실패() {
        // given
        String userId = "user1234";
        Long eventScheduleId = 1L;
        List<Long> eventSeatIds = List.of(1L, 2L);
        ReserveSeatCommand command = new ReserveSeatCommand(userId, eventScheduleId, eventSeatIds);
        LocalDateTime ticketOpenAt = LocalDateTime.now();
        LocalDateTime ticketCloseAt = ticketOpenAt.plusMinutes(5);
        List<SeatSnapshot> seats = List.of(
            new SeatSnapshot(1L, 1000, true),
            new SeatSnapshot(2L, 1000, true));

        int maxReservePerUser = 3;
        when(eventScheduleQueryPort.findById(eventScheduleId)).thenReturn(new EventScheduleSnapshot(1L, ticketOpenAt, ticketCloseAt,
            maxReservePerUser));
        when(eventSeatQueryPort.findBySeatIds(eventSeatIds)).thenReturn(seats);
        when(reservationCountQueryPort.countReservedSeats(userId, eventScheduleId)).thenReturn(2);
        // when
        // then
        assertThatThrownBy(() -> reserveSeatService.reserveSeats(command))
            .isInstanceOf(ReservePerUserLimitException.class)
            .extracting("errorCode").isEqualTo(ReservationErrorCode.RESERVE_PER_USER_LIMIT);

    }

}