package io.github.ktg.ticketing.persistence.user.entity;

import io.github.ktg.ticketing.domain.user.model.Email;
import io.github.ktg.ticketing.domain.user.model.PasswordHash;
import io.github.ktg.ticketing.domain.user.model.User;
import io.github.ktg.ticketing.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * user 테이블 엔티티 클래스
 * - 도메인 <-> 엔티티 변환 기능을 제공 (toDomain, from)
 */
@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    /**
     * 도메인 -> 엔티티 변환 정적 메서드
     * @param user 유저 도메인
     * @return UserJpaEntity
     */
    public static UserJpaEntity from(User user) {
        UUID id = user.getId() == null ? null : UUID.fromString(user.getId());
        return new UserJpaEntity(
            id,
            user.getEmail().value(),
            user.getPassword().value()
        );
    }

    /**
     * 엔티티 -> 도메인 변환 메서드
     * @return User 유저 도메인
     */
    public User toDomain() {
        User withoutId = User.withoutId(
            new Email(email),
            new PasswordHash(password));
        return id == null ? withoutId : withoutId.withId(id.toString());
    }

}
