package com.wanted.naeil.domain.payment.entity;

import com.wanted.naeil.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription")
@Getter
@NoArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "next_reset_at")
    private LocalDateTime nextResetAt;

    @Column(name = "remaining_free_count", nullable = false)
    private int remainingFreeCount;

    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status;

    @Builder
    public Subscription(Payment payment, User user, LocalDateTime startAt,
                        LocalDateTime endAt, LocalDateTime nextResetAt,
                        int remainingFreeCount, Boolean autoRenew,
                        SubscriptionStatus status) {
        this.payment = payment;
        this.user = user;
        this.startAt = startAt;
        this.endAt = endAt;
        this.nextResetAt = nextResetAt;
        this.remainingFreeCount = remainingFreeCount;
        this.autoRenew = autoRenew;
        this.status = status;
    }

    public void updateAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public void updateRemainingFreeCount(int remainingFreeCount) {
        this.remainingFreeCount = remainingFreeCount;
    }

    public void updateStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public void renew(LocalDateTime newEndAt, LocalDateTime newNextResetAt) {
        this.endAt = newEndAt;
        this.nextResetAt = newNextResetAt;
        this.status = SubscriptionStatus.ACTIVE;
    }
}
