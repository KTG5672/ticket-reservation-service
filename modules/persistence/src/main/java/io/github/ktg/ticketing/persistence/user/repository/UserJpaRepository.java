package io.github.ktg.ticketing.persistence.user.repository;

import io.github.ktg.ticketing.persistence.user.entity.UserJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserJpaEntity Spring Data JPA Repository
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);
}
