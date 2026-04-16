package com.wanted.naeil.domain.admin.entity;

import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.live.entity.LiveLecture;
import com.wanted.naeil.domain.settlement.entity.Settlement;
import com.wanted.naeil.domain.user.entity.InstructorApplications;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;


@Entity
@Table(name = "admin_approvals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminApproval extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Long approvalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // 실시간 강의 번호 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "live_id")
    private LiveLecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id")
    private Settlement settlement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private InstructorApplications instructorApplications;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private User instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false, length = 30)
    private ApprovalRequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
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

    @Builder
    public AdminApproval(Course course, ApprovalRequestType requestType) {
        this.course = course;
        this.requestType = requestType;
        this.status = ApprovalStatus.PENDING;
    }
    @Builder
    public AdminApproval(InstructorApplications applications) {
        this.instructorApplications = applications;
        this.requestType = ApprovalRequestType.INSTRUCTOR_REGISTER;
        this.status = ApprovalStatus.PENDING;
    }
    @Builder
    public AdminApproval(LiveLecture lecture) {
        this.lecture = lecture;
        this.requestType = ApprovalRequestType.LIVE_REGISTER;
        this.status = ApprovalStatus.PENDING;
    }
    @Builder
    public AdminApproval(Settlement settlement) {
        this.settlement = settlement;
        this.requestType = ApprovalRequestType.SETTLEMENT_REGISTER;
        this.status = ApprovalStatus.PENDING;
    }
}
