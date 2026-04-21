package com.wanted.naeil.domain.settlement.entity;

import com.wanted.naeil.domain.settlement.entity.enums.SettlementStatus;
import com.wanted.naeil.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    private String settlementMonth;

    @Column(name = "total_sales_amount", nullable = false)
    private int totalSalesAmount;

    @Column(name = "platform_fee", nullable = false)
    private int platformFee;

    @Column(name = "final_amount", nullable = false)
    private int finalAmount;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Column(name = "requested_amount", nullable = false)
    private int requestedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SettlementStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettlementDetail> details = new ArrayList<>();

    public void addDetail(SettlementDetail detail) {
        this.details.add(detail);
        detail.assignSettlement(this);
    }

    public void request() {
        if (this.status != SettlementStatus.READY) {
            throw new IllegalStateException("신청 가능한 정산만 신청할 수 있습니다.");
        }

        this.status = SettlementStatus.PENDING;
    }

    public void cancel() {
        if (this.status != SettlementStatus.PENDING) {
            throw new IllegalStateException("대기 중인 정산만 취소할 수 있습니다.");
        }

        this.status = SettlementStatus.CANCELED;
    }

    @Builder
    public Settlement(User instructor,
                      User admin,
                      String settlementMonth,
                      int totalSalesAmount,
                      int platformFee,
                      int finalAmount,
                      int totalAmount,
                      int requestedAmount,
                      SettlementStatus status,
                      LocalDateTime createdAt,
                      LocalDateTime completedAt) {
        this.instructor = instructor;
        this.admin = admin;
        this.settlementMonth = settlementMonth;
        this.totalSalesAmount = totalSalesAmount;
        this.platformFee = platformFee;
        this.finalAmount = finalAmount;
        this.totalAmount = totalAmount;
        this.requestedAmount = requestedAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }
}