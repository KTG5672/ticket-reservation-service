package io.github.ktg.ticketing.entity;

import io.github.ktg.ticketing.domain.EventSeatStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;

@Table(name = "event_seats")
@Entity
@Getter
public class EventSeatJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_schedule_id")
    private EventScheduleJpaEntity eventSchedule;

    @OneToMany(mappedBy = "seat")
    private List<ReservationSeatJpaEntity> reservationSeats;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventSeatStatus status;

    @Column(name = "zone")
    private String zone;

    @Column(name = "no")
    private Integer no;

    @Column(name = "price", nullable = false)
    private int price;

}
