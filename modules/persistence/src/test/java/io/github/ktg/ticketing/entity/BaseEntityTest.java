package io.github.ktg.ticketing.entity;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ktg.ticketing.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class BaseEntityTest {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * JPA Auditing 생성일, 생성자 테스트
     */
    @Test
    @DisplayName("JPA Auditing 생성일, 생성자 테스트")
    void auditing_생성일_생성자_테스트() {
        // given
        TestEntity testEntity = new TestEntity();
        // when
        entityManager.persist(testEntity);
        // then
        assertThat(testEntity.getCreatedAt()).isNotNull();
        assertThat(testEntity.getCreatedBy()).isNotNull();
    }

    /**
     * JPA Auditing 수정일, 수정자 테스트
     * - 생성일 <= 수정일
     */
    @Test
    @DisplayName("JPA Auditing 수정일, 수정자 테스트")
    void auditing_수정일_수정자_테스트() {
        // given
        TestEntity testEntity = new TestEntity("name1");
        // when
        entityManager.persist(testEntity);

        TestEntity findEntity = entityManager.find(TestEntity.class, testEntity.getId());
        findEntity.changeName("name2");
        entityManager.flush();
        entityManager.clear();
        // then
        assertThat(findEntity.getUpdatedAt()).isNotNull();
        assertThat(findEntity.getUpdatedBy()).isNotNull();
        assertThat(findEntity.getUpdatedAt()).isAfter(findEntity.getCreatedAt());
    }


}