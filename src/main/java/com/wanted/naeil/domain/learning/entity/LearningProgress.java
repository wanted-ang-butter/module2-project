package com.wanted.naeil.domain.learning.entity;

import com.wanted.naeil.domain.course.entity.Section;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "learning_progress")
@Getter
@NoArgsConstructor
public class LearningProgress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false, length = 20)
    private ProgressStatus status; // IN_PROGRESS, COMPLETED

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt; // 최근 학습 일시

    @Builder
    public LearningProgress(User user, Section section) {
        this.user = user;
        this.section = section;
        this.status = ProgressStatus.NOT_STARTED;
        this.lastAccessedAt = LocalDateTime.now();
    }

    // 비지니스 로직

    public void start() {
        if (this.status == ProgressStatus.NOT_STARTED) {
            this.status = ProgressStatus.IN_PROGRESS;
        }
        this.lastAccessedAt = LocalDateTime.now();
    }

    public void stop() {
        // 이미 완료(COMPLETED)된 상태라면 상태를 되돌리지 않음
        // '수강 전(NOT_STARTED)'일 때만 '수강 중(IN_PROGRESS)'으로 변경
        if (this.status == ProgressStatus.NOT_STARTED) {
            this.status = ProgressStatus.IN_PROGRESS;
        }

        // 중단 시점의 시간을 마지막 접속 시간으로 기록
//        this.updateLastAccessedAt();
    }

    public void complete() {
        this.status = ProgressStatus.COMPLETED;
        this.lastAccessedAt = LocalDateTime.now();
    }


}
