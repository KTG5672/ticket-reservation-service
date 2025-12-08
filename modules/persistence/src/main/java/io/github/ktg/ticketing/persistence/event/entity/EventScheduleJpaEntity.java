package io.github.ktg.ticketing.persistence.event.entity;

import io.github.ktg.ticketing.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "event_schedules")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EventScheduleJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventJpaEntity event;

    @Column(name = "event_start_at")
    private LocalDateTime eventStartAt;

    @Column(name = "max_reserve_per_user")
    private Integer maxReservePerUser;

    @Column(name = "ticket_open_at")
    private LocalDateTime ticketOpenAt;

    @Column(name = "ticket_close_at")
    private LocalDateTime ticketCloseAt;

}
