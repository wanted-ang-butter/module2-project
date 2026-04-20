package com.wanted.naeil.domain.user.entity;

import com.wanted.naeil.domain.course.entity.Category;
import com.wanted.naeil.domain.user.entity.enums.ApplicationStatus;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "instructor_applications")
@Getter
@NoArgsConstructor
public class InstructorApplications extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private String title;

    private String introduction;

    private String accountNumber;

    @Column(columnDefinition = "TEXT")
    private String career;

    @Column(name = "proof_file", columnDefinition = "TEXT")
    private String proofFileUrl;

    @Column(name = "face_img")
    private String faceImgUrl;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(columnDefinition = "TEXT")
    private String rejectReason;

    // 비즈니스 로직
    @Builder
    public InstructorApplications(User user, Category category, String title,
                                 String introduction, String career,
                                 String proofFileUrl, String accountNumber, String faceImgUrl) {
        this.user = user;
        this.category = category;
        this.title = title;
        this.introduction = introduction;
        this.career = career;
        this.proofFileUrl = proofFileUrl;
        this.accountNumber = accountNumber;
        this.faceImgUrl = faceImgUrl;
        this.status = ApplicationStatus.PENDING;
    }

    public void approve() {
        this.status = ApplicationStatus.APPROVED;
    }

    public void reject(String rejectReason) {
        this.status = ApplicationStatus.REJECTED;
        this.rejectReason = rejectReason;
    }
}
