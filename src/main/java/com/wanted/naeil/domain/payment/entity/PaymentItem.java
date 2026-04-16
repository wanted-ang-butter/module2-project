package com.wanted.naeil.domain.payment.entity;

import com.wanted.naeil.domain.course.entity.Course;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_items")
@Getter
@NoArgsConstructor
public class PaymentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private PaymentItemType itemType;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "discount_amount", nullable = false)
    private int discountAmount;

    @Column(name = "final_price", nullable = false)
    private int finalPrice;

    // 비지니스 로직

    @Builder
    public PaymentItem(Course course, PaymentItemType itemType, int price, int discountAmount, int finalPrice) {
        this.course = course;
        this.itemType = itemType;
        this.price = price;
        this.discountAmount = discountAmount;
        this.finalPrice = finalPrice;
    }

    public void assignPayment(Payment payment) {
        this.payment = payment;
    }
}
