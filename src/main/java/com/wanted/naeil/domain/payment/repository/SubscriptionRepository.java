package com.wanted.naeil.domain.payment.repository;

import com.wanted.naeil.domain.payment.entity.Subscription;
import com.wanted.naeil.domain.payment.entity.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // 현재 가장 유효한 활성 구독을 갖고옴
    Optional<Subscription> findTopByUserIdAndStatusOrderByEndAtDesc(Long userId, SubscriptionStatus status);
}