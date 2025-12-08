package io.github.ktg.ticketing.persistence.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.ktg.ticketing.domain.event.model.EventSeatStatus;
import io.github.ktg.ticketing.domain.reservation.dto.SeatSnapshot;
import io.github.ktg.ticketing.domain.reservation.model.Reservation;
import io.github.ktg.ticketing.persistence.TestContainerForMySQL;
import io.github.ktg.ticketing.persistence.config.JpaAuditingConfig;
import io.github.ktg.ticketing.persistence.config.QuerydslConfig;
import io.github.ktg.ticketing.persistence.event.entity.EventScheduleJpaEntity;
import io.github.ktg.ticketing.persistence.event.entity.EventSeatJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import({QuerydslConfig.class, JpaAuditingConfig.class})
@ActiveProfiles("test")
class ReservationQueryRepositoryTest extends TestContainerForMySQL {

    @Autowired
    JPAQueryFactory jpaQueryFactory;

    ReservationQueryRepository queryRepository;

    @Autowired
    ReservationJpaRepository reservationJpaRepository;

    @PersistenceContext
    EntityManager entityManager;

    ReservationRepositoryImpl reservationRepository;

    @BeforeEach
    void setUp() {
        queryRepository = new ReservationQueryRepository(jpaQueryFactory);
        reservationRepository = new ReservationRepositoryImpl(reservationJpaRepository);
    }


    @Test
    @DisplayName("유저,일정 기준으로 예약된 좌석 수를 조회한다.")
    void countReservedSeats() {
        // given
        String userId = "user123";
        EventScheduleJpaEntity eventScheduleJpaEntity = new EventScheduleJpaEntity(null, null, null,
            null, null, null);
        entityManager.persist(eventScheduleJpaEntity);
        EventSeatJpaEntity eventSeatJpaEntity1 = new EventSeatJpaEntity(null, eventScheduleJpaEntity, EventSeatStatus.HELD, null,
            null, 1000);
        EventSeatJpaEntity eventSeatJpaEntity2 = new EventSeatJpaEntity(null, eventScheduleJpaEntity, EventSeatStatus.HELD, null,
            null, 1000);
        entityManager.persist(eventSeatJpaEntity1);
        entityManager.persist(eventSeatJpaEntity2);
        Reservation waitingPayment = Reservation.createWaitingPayment(userId, List.of(
            new SeatSnapshot(eventSeatJpaEntity1.getId(), 1000, true),
            new SeatSnapshot(eventSeatJpaEntity2.getId(), 1000, true)
        ));
        reservationRepository.save(waitingPayment);
        entityManager.flush();
        entityManager.clear();
        // when
        int count = queryRepository.countReservedSeats(userId, eventScheduleJpaEntity.getId());
        // then
        assertThat(count).isEqualTo(2);

    }
}