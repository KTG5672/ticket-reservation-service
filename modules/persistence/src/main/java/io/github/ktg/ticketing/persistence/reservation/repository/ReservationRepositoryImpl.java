package io.github.ktg.ticketing.persistence.reservation.repository;

import io.github.ktg.ticketing.domain.reservation.model.Reservation;
import io.github.ktg.ticketing.domain.reservation.port.out.ReservationRepository;
import io.github.ktg.ticketing.persistence.reservation.entity.ReservationJpaEntity;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * ReservationRepository port 구현체
 * - Spring Data JPA 인터페이스 인 ReservationRepository 를 사용하여 영속화
 */
@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Optional<Reservation> findById(Long reservationId) {
        ReservationJpaEntity entity = reservationJpaRepository.findById(reservationId).orElse(null);
        return entity == null ? Optional.empty() : Optional.of(entity.toDomain());
    }

    @Override
    public Reservation save(Reservation reservation) {
        ReservationJpaEntity entity = ReservationJpaEntity.from(reservation);
        return reservationJpaRepository.save(entity).toDomain();
    }
}
