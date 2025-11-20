package io.github.ktg.ticketing.domain.reservation.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ktg.ticketing.domain.event.model.EventSeat;
import io.github.ktg.ticketing.domain.reservation.exception.ReservationErrorCode;
import io.github.ktg.ticketing.domain.reservation.exception.ReservationNotValidException;
import io.github.ktg.ticketing.domain.reservation.exception.ReservationStatusNotValidException;
import java.lang.reflect.Constructor;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReservationTest {

    @Test
    @DisplayName("예약은 하나 이상의 좌석을 가져야 한다.")
    void 예약은_하나_이상의_좌석을_가져야_한다() {
        // given
        String userId = "user123";
        // when
        // then
        assertThatThrownBy(() ->
            Reservation.createWaitingPayment(userId, null))
            .isInstanceOf(ReservationNotValidException.class)
            .extracting("errorCode")
            .isEqualTo(ReservationErrorCode.NOT_ENOUGH_RESERVE_SEATS);
    }

    @Test
    @DisplayName("예약 만료는 결제 대기 상태에서 가능")
    void 예약_만료는_결제_대기_상태에서_가능() throws Exception {
        // given
        String userId = "userId";
        Reservation reservation = Reservation.createWaitingPayment(userId, getMockEventSeats());
        // when
        reservation.expire();
        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
    }


    @Test
    @DisplayName("예약 만료는 결제 대기 상태가 아니면 불가능")
    void 예약_만료는_결제_대기_상태가_아니면_불가능() throws Exception {
        // given
        String userId = "userId";
        Reservation reservation = Reservation.createWaitingPayment(userId, getMockEventSeats());
        reservation.cancel();
        // when
        // then
        assertThatThrownBy(reservation::expire)
            .isInstanceOf(ReservationStatusNotValidException.class)
            .extracting("errorCode")
            .isEqualTo(ReservationErrorCode.INVALID_STATUS_FOR_EXPIRE);
    }


    @Test
    @DisplayName("예약 완료는 결제 대기 상태면 가능")
    void 예약_완료는_결제_대기_상태면_가능() throws Exception {
        // given
        String userId = "userId";
        Reservation reservation = Reservation.createWaitingPayment(userId, getMockEventSeats());
        // when
        reservation.complete();
        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.COMPLETED);
    }

    @Test
    @DisplayName("예약 완료는 결제 대기 상태가 아니면 불가능")
    void 예약_완료는_결제_대기_상태가_아니면_불가능() throws Exception {
        // given
        String userId = "userId";
        Reservation reservation = Reservation.createWaitingPayment(userId, getMockEventSeats());
        reservation.cancel();
        // when
        // then
        assertThatThrownBy(reservation::complete)
            .isInstanceOf(ReservationStatusNotValidException.class)
            .extracting("errorCode")
            .isEqualTo(ReservationErrorCode.INVALID_STATUS_FOR_COMPLETE);
    }

    @Test
    @DisplayName("예약 취소는 결제 대기 상태 또는 완료 상태에서 가능")
    void 예약_취소는_결제_대기_상태_또는_완료_상태에서_가능() throws Exception {
        // given
        String userId = "userId";
        Reservation waitingPaymentReservation = Reservation.createWaitingPayment(userId, getMockEventSeats());
        Reservation completeReservation = Reservation.createWaitingPayment(userId, getMockEventSeats());
        completeReservation.complete();
        // when
        waitingPaymentReservation.cancel();
        completeReservation.cancel();
        // then
        assertThat(waitingPaymentReservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
        assertThat(completeReservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
    }

    @Test
    @DisplayName("예약 취소는 결제 대기 상태 또는 완료 상태가 아니면 불가능")
    void 예약_취소는_결제_대기_상태_또는_완료_상태가_아니면_불가능() throws Exception {
        // given
        String userId = "userId";
        Reservation reservation = Reservation.createWaitingPayment(userId, getMockEventSeats());
        reservation.expire();
        // when
        // then
        assertThatThrownBy(reservation::cancel)
            .isInstanceOf(ReservationStatusNotValidException.class)
            .extracting("errorCode")
            .isEqualTo(ReservationErrorCode.INVALID_STATUS_FOR_CANCEL);
    }

    List<EventSeat> getMockEventSeats() throws Exception {
        Class<?> clazz = Class.forName("io.github.ktg.ticketing.domain.event.model.EventSeat");
        Constructor<?> constructor = clazz.getConstructor();
        constructor.setAccessible(true);
        return List.of((EventSeat) constructor.newInstance(),
            (EventSeat) constructor.newInstance());
    }
}