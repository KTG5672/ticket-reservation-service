package io.github.ktg.ticketing.persistence.event.entity;

import io.github.ktg.ticketing.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;

@Table(name = "events")
@Entity
@Getter
public class EventJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "place")
    private String place;

    @Lob
    @Column(name = "event_info", columnDefinition = "MEDIUMTEXT")
    private String eventInfo;

    @Column(name = "ticket_open_at")
    private LocalDateTime ticketOpenAt;

    @Column(name = "ticket_close_at")
    private LocalDateTime ticketCloseAt;

}
