package io.github.ktg.ticketing.persistence.user.repository;

import io.github.ktg.ticketing.domain.user.model.User;
import io.github.ktg.ticketing.domain.user.port.out.UserRepository;
import io.github.ktg.ticketing.persistence.user.entity.UserJpaEntity;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * UserRepository port 구현체
 * - Spring Data JPA 인터페이스 인 UserJpaRepository 를 사용하여 영속화
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserJpaEntity> resultEntity = userJpaRepository.findByEmail(email);
        return resultEntity.map(UserJpaEntity::toDomain);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserJpaEntity.from(user);
        return userJpaRepository.save(entity).toDomain();
    }

}
