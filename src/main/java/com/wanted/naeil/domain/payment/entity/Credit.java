package com.wanted.naeil.domain.payment.entity;

import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "credit")
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

    // 비지니스 로직

    // 최소 충전 금액 및 충전 단위 설정
    private void validateChargeAmount(int amount) {
        if (amount < 10000) {
            throw new IllegalArgumentException("최소 충전 금액은 10,000 크레딧입니다.");
        }
        if (amount % 1000 != 0) {
            throw new IllegalArgumentException("1,000 크레딧 단위로만 충전할 수 있습니다.");
        }
    }

    public void charge(int amount) {
        validateChargeAmount(amount);
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
