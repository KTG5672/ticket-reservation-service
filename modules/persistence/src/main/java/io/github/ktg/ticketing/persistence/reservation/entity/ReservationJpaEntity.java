package io.github.ktg.ticketing.persistence.reservation.entity;

import io.github.ktg.ticketing.domain.reservation.model.Reservation;
import io.github.ktg.ticketing.domain.reservation.model.ReservationSeat;
import io.github.ktg.ticketing.domain.reservation.model.ReservationStatus;
import io.github.ktg.ticketing.persistence.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * reservations 테이블 엔티티 클래스
 * - 도메인 <-> 엔티티 변환 기능을 제공 (toDomain, from)
 */
@Table(name = "reservations")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReservationJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationSeatJpaEntity> reservationSeats = new ArrayList<>();

    /**
     * 엔티티 -> 도메인 메서드
     * @return Reservation 예약 도메인
     */
    public Reservation toDomain() {
        List<ReservationSeat> reservationSeatDomains = reservationSeats.stream()
            .map(ReservationSeatJpaEntity::toDomain).toList();
        return Reservation.reconstruct(id, userId, status, new ArrayList<>(reservationSeatDomains));
    }

    /**
     * 도메인 -> 엔티티 생성 정적 메서드
     * @param reservation 예약 도메인
     * @return ReservationJpaEntity 예약 엔티티
     */
    public static ReservationJpaEntity from(Reservation reservation) {
        ReservationJpaEntity reservationJpaEntity = new ReservationJpaEntity(
            reservation.getId(),
            reservation.getUserId(),
            reservation.getStatus(),
            new ArrayList<>()
        );
        List<ReservationSeatJpaEntity> seats = reservation.getSeats()
            .stream()
            .map(seat -> ReservationSeatJpaEntity.from(reservationJpaEntity, seat))
            .toList();
        reservationJpaEntity.assignReservationSeats(seats);
        return reservationJpaEntity;
    }

    /**
     * 예약 좌석 연관 관계 편의 메서드
     * @param seats 예약 좌석 엔티티 리스트
     */
    public void assignReservationSeats(List<ReservationSeatJpaEntity> seats) {
        this.reservationSeats.clear();
        this.reservationSeats.addAll(seats);
        for (ReservationSeatJpaEntity seat : seats) {
            seat.assignReservation(this);
        }
    }

}
