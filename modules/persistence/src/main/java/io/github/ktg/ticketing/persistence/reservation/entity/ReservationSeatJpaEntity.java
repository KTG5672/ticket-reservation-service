package io.github.ktg.ticketing.persistence.reservation.entity;

import io.github.ktg.ticketing.domain.reservation.model.ReservationSeat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * reservation_seats 테이블 엔티티 클래스
 * - 도메인 <-> 엔티티 변환 기능을 제공 (toDomain, from)
 */
@Table(name = "reservation_seats")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationSeatJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private ReservationJpaEntity reservation;

    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    @Column(name = "price", nullable = false)
    private int price;

    /**
     * 엔티티 -> 도메인 생성 메서드
     * @return ReservationSeat 예약 좌석 도메인
     */
    public ReservationSeat toDomain() {
        return ReservationSeat.reconstruct(id, seatId, price);
    }

    /**
     * 도메인 -> 엔티티 생성 정적 메서드
     * @param reservationJpaEntity 예약 엔티티
     * @param reservationSeat 예약 좌석 도메인
     * @return ReservationSeatJpaEntity 예약 좌석 엔티티
     */
    public static ReservationSeatJpaEntity from(ReservationJpaEntity reservationJpaEntity,
        ReservationSeat reservationSeat) {
        return new ReservationSeatJpaEntity(
            reservationSeat.getId(),
            reservationJpaEntity,
            reservationSeat.getSeatId(),
            reservationSeat.getPrice()
        );
    }

    /**
     * 예약 연관 관계 편의 메서드
     * @param reservationJpaEntity 예약 엔티티
     */
    public void assignReservation(ReservationJpaEntity reservationJpaEntity) {
        this.reservation = reservationJpaEntity;
    }
}
