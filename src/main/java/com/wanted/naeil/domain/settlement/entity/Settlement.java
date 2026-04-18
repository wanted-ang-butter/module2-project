package com.wanted.naeil.domain.settlement.entity;

import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "settlement")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    @Column(name = "settlement_month", nullable = false, length = 7)
    private String settlementMonth; // ex) 2026-04

    @Column(name = "total_sales_amount", nullable = false)
    private int totalSalesAmount; // 정산 대상 총 판매 금액

    @Column(name = "platform_fee", nullable = false)
    private int platformFee; // 플랫폼 수수료

    @Column(name = "final_amount", nullable = false)
    private int finalAmount; // 실제 정산 금액

    @Column(name = "total_amount", nullable = false)
    private int totalAmount; // 누적 정산 완료 금액

    @Column(name = "requested_amount", nullable = false)
    private int requestedAmount; // 강사가 이번에 신청한 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SettlementStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettlementDetail> details = new ArrayList<>();


    // 비지니스 로직

    public void addDetail(SettlementDetail detail) {
        this.details.add(detail);
        detail.assignSettlement(this);
    }

    public void approve(User admin) {
        this.admin = admin;
        this.status = SettlementStatus.APPROVED;
        this.completedAt = LocalDateTime.now();
    }

    public void reject(User admin, String rejectReason) {
        this.admin = admin;
        this.status = SettlementStatus.REJECTED;
    }

    public void cancel() {
        if (this.status != SettlementStatus.PENDING) {
            throw new IllegalStateException("대기 중인 정산만 취소할 수 있습니다.");
        }
        this.status = SettlementStatus.CANCELED;
    }
}
