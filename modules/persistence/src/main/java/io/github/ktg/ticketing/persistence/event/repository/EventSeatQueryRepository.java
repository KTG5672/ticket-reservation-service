package io.github.ktg.ticketing.persistence.event.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.ktg.ticketing.domain.event.dto.SeatReservationInfo;
import io.github.ktg.ticketing.domain.event.model.EventSeatStatus;
import io.github.ktg.ticketing.persistence.event.entity.QEventSeatJpaEntity;
import jakarta.persistence.LockModeType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 좌석 조회 전용 Repository
 * - JPAQueryFactory (Querydsl) 사용하여 쿼리 생성
 */
@Repository
@RequiredArgsConstructor
public class EventSeatQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 좌석 조회 with Lock
     * @param eventScheduleId 일정 식별자
     * @param seatIds 좌석 식별자 list
     * @return SeatReservationInfo
     */
    @Transactional
    public List<SeatReservationInfo> findWithLockByEventScheduleIdAndSeatIds(Long eventScheduleId, List<Long> seatIds) {

        QEventSeatJpaEntity eventSeat = QEventSeatJpaEntity.eventSeatJpaEntity;

        return jpaQueryFactory
            .select(Projections.constructor(SeatReservationInfo.class,
                eventSeat.id,
                eventSeat.price,
                eventSeat.status.eq(EventSeatStatus.AVAILABLE))
            )
            .from(eventSeat)
            .where(
                eventSeat.eventSchedule.id.eq(eventScheduleId),
                eventSeat.id.in(seatIds)
            )
            .orderBy(eventSeat.id.asc()) // 데드락 방지
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetch();
    }

}
