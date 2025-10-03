package io.github.ktg.ticketing.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 테스트용 Entity
 */
@Entity
@Getter
@NoArgsConstructor
public class TestEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public TestEntity(String name) {
        this.name = name;
    }

    public void changeName(String name) {
        this.name = name;
    }

}
