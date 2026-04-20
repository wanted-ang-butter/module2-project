package com.wanted.naeil.domain.live.entity;

import com.wanted.naeil.domain.live.entity.enums.LiveLectureStatus;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "live_lectures")
@Getter
@NoArgsConstructor
public class LiveLecture extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "live_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;

    @Column(name = "current_count", nullable = false)
    private int currentCount;

    @Column(name = "reservation_start_at")
    private LocalDateTime reservationStartAt;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "streaming_url", length = 500)
    private String streamingUrl;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LiveLectureStatus status; // RESERVED, CANCELED

    @Builder
    public LiveLecture(User instructor, String title, String description, int maxCapacity,
                       LocalDateTime reservationStartAt,
                       LocalDateTime startAt, LocalDateTime endAt,
                       String streamingUrl) {
        this.instructor = instructor;
        this.title = title;
        this.description = description;
        this.currentCount = 0;
        this.maxCapacity = maxCapacity;
        this.reservationStartAt = reservationStartAt;
        this.startAt = startAt;
        this.endAt = endAt;
        this.streamingUrl = streamingUrl;
        this.status = LiveLectureStatus.PENDING;
    }

    // 예약 증가 로직 (정원 체크)
    public void incrementReservation() {
        if (this.currentCount >= this.maxCapacity) {
            throw new IllegalStateException("정원이 초과되었습니다.");
        }
        this.currentCount++;
    }

    // 상태 변경 메서드
    public void changeStatus(LiveLectureStatus status) {
        this.status = status;
    }
}
