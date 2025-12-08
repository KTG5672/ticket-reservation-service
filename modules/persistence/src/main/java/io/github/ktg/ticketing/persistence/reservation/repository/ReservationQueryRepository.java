package io.github.ktg.ticketing.persistence.reservation.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.ktg.ticketing.domain.reservation.model.ReservationStatus;
import io.github.ktg.ticketing.persistence.event.entity.QEventSeatJpaEntity;
import io.github.ktg.ticketing.persistence.reservation.entity.QReservationJpaEntity;
import io.github.ktg.ticketing.persistence.reservation.entity.QReservationSeatJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 예약 조회 전용 Repository
 * - JPAQueryFactory (Querydsl) 사용하여 쿼리 생성
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 특정 유저, 일정에 예약(결제 대기,완료)된 좌석 수 조회
     * @param userId 유저 식별자
     * @param scheduleId 이벤트 일정 식별자
     * @return 예약 좌석 수
     */
    public int countReservedSeats(String userId, Long scheduleId) {
        QReservationJpaEntity reservation = QReservationJpaEntity.reservationJpaEntity;
        QReservationSeatJpaEntity reservationSeat = QReservationSeatJpaEntity.reservationSeatJpaEntity;
        QEventSeatJpaEntity eventSeat = QEventSeatJpaEntity.eventSeatJpaEntity;
        Long count = jpaQueryFactory.select(reservationSeat.id.count())
            .from(reservationSeat)
            .join(reservationSeat.reservation, reservation)
            .join(eventSeat).on(reservationSeat.seatId.eq(eventSeat.id))
            .where(
                reservation.userId.eq(userId),
                reservation.status.in(ReservationStatus.WAITING_PAYMENT, ReservationStatus.COMPLETED),
                eventSeat.eventSchedule.id.eq(scheduleId)
            ).fetchOne();
        return count == null ? 0 : count.intValue();
    }

}
