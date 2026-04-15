package com.wanted.naeil.domain.payment.entity;

import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "credits")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credit extends BaseTimeEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "balance", nullable = false)
    @Builder.Default
    private int balance = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 비지니스 로직

    public void charge(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        this.balance += amount;
    }

    public void deduct(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("차감 금액은 0보다 커야 합니다.");
        }
        if (this.balance < amount) {
            throw new IllegalArgumentException("크레딧이 부족합니다.");
        }
        this.balance -= amount;
    }
}
