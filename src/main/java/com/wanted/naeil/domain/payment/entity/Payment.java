package com.wanted.naeil.domain.payment.entity;

import com.wanted.naeil.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;   // 총 원가

    @Column(name = "discount_amount", nullable = false)
    private int discountAmount; // 총 할인금액

    @Column(name = "final_amount", nullable = false)
    private int finalAmount;   // 최종 결제 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentItem> paymentItems = new ArrayList<>();

    public Payment(User user, int totalAmount, int discountAmount, int finalAmount) {
        this.user = user;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.status = PaymentStatus.READY;
    }

    // TODO : 로직 뭔지 찾아보기
//    public void addPaymentItem(PaymentItemType paymentItemType) {
//        this.paymentItems.add(paymentItemType);
//        paymentItemType.assignPayment(this);
//    }

    public void markSuccess() {
        this.status = PaymentStatus.SUCCESS;
        this.paidAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }
}
