package com.wanted.naeil.domain.settlement.entity;

import com.wanted.naeil.domain.settlement.entity.enums.SettlementStatus;
import com.wanted.naeil.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public void accumulateSale(int saleAmount, int feeAmount, int settlementAmount) {
        this.totalSalesAmount += saleAmount;
        this.platformFee += feeAmount;
        this.finalAmount += settlementAmount;
        this.totalAmount += settlementAmount;

        if (this.status == SettlementStatus.READY
                || this.status == SettlementStatus.REJECTED
                || this.status == SettlementStatus.CANCELED) {
            this.status = SettlementStatus.READY;
            this.requestedAmount = this.finalAmount;
        }
    }

    public Optional<SettlementDetail> findDetailByCourseId(Long courseId) {
        return details.stream()
                .filter(detail -> detail.getCourse().getId().equals(courseId))
                .findFirst();
    }

    public void request() {
        if (this.status != SettlementStatus.READY
                && this.status != SettlementStatus.REJECTED
                && this.status != SettlementStatus.CANCELED) {
            throw new IllegalStateException("Only ready, rejected, or canceled settlements can be requested.");
        }

        this.status = SettlementStatus.PENDING;
        this.admin = null;
        this.completedAt = null;
        this.requestedAmount = this.finalAmount;
    }

    public void cancel() {
        if (this.status != SettlementStatus.PENDING) {
            throw new IllegalStateException("Only pending settlements can be canceled.");
        }

        this.status = SettlementStatus.CANCELED;
        this.admin = null;
        this.completedAt = null;
    }

    public void approve(User admin) {
        if (this.status != SettlementStatus.PENDING) {
            throw new IllegalStateException("Only pending settlements can be approved.");
        }

        this.status = SettlementStatus.APPROVED;
        this.admin = admin;
        this.completedAt = LocalDateTime.now();
    }

    public void reject(User admin) {
        if (this.status != SettlementStatus.PENDING) {
            throw new IllegalStateException("Only pending settlements can be rejected.");
        }

        this.status = SettlementStatus.REJECTED;
        this.admin = admin;
        this.completedAt = LocalDateTime.now();
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
