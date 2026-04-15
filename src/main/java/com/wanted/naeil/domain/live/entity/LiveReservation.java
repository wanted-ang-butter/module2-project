package com.wanted.naeil.domain.live.entity;

import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "live_reservations")
@Getter
@NoArgsConstructor
public class LiveReservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "live_id", nullable = false)
    private LiveLecture liveLecture;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LiveReservationStatus status; // RESERVED, CANCELED

    @Builder
    public LiveReservation(User user, LiveLecture liveLecture) {
        this.user = user;
        this.liveLecture = liveLecture;
        this.status = LiveReservationStatus.RESERVED;
    }

    // 취소
    public void cancel() {
        this.status = LiveReservationStatus.CANCELED;
    }
}
