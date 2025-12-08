package io.github.ktg.ticketing.persistence.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.ktg.ticketing.domain.reservation.dto.SeatSnapshot;
import io.github.ktg.ticketing.domain.reservation.model.Reservation;
import io.github.ktg.ticketing.domain.reservation.model.ReservationSeat;
import io.github.ktg.ticketing.persistence.TestContainerForMySQL;
import io.github.ktg.ticketing.persistence.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class ReservationRepositoryImplTest extends TestContainerForMySQL {

    @Autowired
    ReservationJpaRepository reservationJpaRepository;

    @PersistenceContext
    EntityManager entityManager;

    ReservationRepositoryImpl reservationRepository;

    @BeforeEach
    void setUp() {
        reservationRepository = new ReservationRepositoryImpl(reservationJpaRepository);
    }

    @Test
    @DisplayName("예약 entity 저장 테스트")
    void 예약_entity_저장_테스트() {
        // given
        Reservation waitingPayment = Reservation.createWaitingPayment("user123", List.of(
            new SeatSnapshot(1L, 1000, true),
            new SeatSnapshot(2L, 1000, true)
            ));
        // when
        Reservation saved = reservationRepository.save(waitingPayment);
        // then
        assertThat(saved.getId()).isNotNull();

    }

    @Test
    @DisplayName("예약 entity 조회 테스트")
    void 예약_entity_조회_테스트() {
        // given
        Reservation waitingPayment = Reservation.createWaitingPayment("user123", List.of(
            new SeatSnapshot(1L, 1000, true),
            new SeatSnapshot(2L, 1000, true)
        ));
        Reservation saved = reservationRepository.save(waitingPayment);
        Long reservationId = saved.getId();
        entityManager.flush();
        entityManager.clear();
        // when
        Optional<Reservation> result = reservationRepository.findById(reservationId);
        // then
        if (result.isEmpty()) {
            fail();
        }
        Reservation reservation = result.get();
        assertThat(reservation).isNotNull();
        List<ReservationSeat> seats = reservation.getSeats();

        seats.forEach(seat -> {
            assertThat(seat.getId()).isNotNull();
            assertThat(seat.getSeatId()).isNotNull();
        });
    }
}