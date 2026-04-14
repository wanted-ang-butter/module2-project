package com.wanted.naeil.domain.admin.entity;

import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.live.entity.LiveLecture;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course_approval")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseApproval extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Long approvalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = true)
    private Course course;

    // 실시간 강의 번호 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "live_id", nullable = true)
    private LiveLecture lecture;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false, length = 20)
    private ApprovalRequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    // 비지니스 로직

    public void approve(User admin) {
        this.status = ApprovalStatus.APPROVED;
        this.admin = admin;
    }

    public void reject(User admin, String rejectReason) {
        this.status = ApprovalStatus.REJECTED;
        this.admin = admin;
        this.rejectReason = rejectReason;
    }
}
