package io.github.ktg.ticketing.persistence.reservation.repository;

import io.github.ktg.ticketing.persistence.reservation.entity.ReservationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ReservationJpaEntity Spring Data JPA Repository
 */
public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {
}
