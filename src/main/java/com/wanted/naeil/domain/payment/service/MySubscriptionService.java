package com.wanted.naeil.domain.payment.service;

import com.wanted.naeil.domain.payment.dto.response.MySubscriptionResponse;
import com.wanted.naeil.domain.payment.entity.Subscription;
import com.wanted.naeil.domain.payment.entity.enums.PlanType;
import com.wanted.naeil.domain.payment.entity.enums.SubscriptionStatus;
import com.wanted.naeil.domain.payment.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MySubscriptionService {

    private static final int TOTAL_FREE_COUNT = 3;

    private final SubscriptionRepository subscriptionRepository;

    public MySubscriptionResponse getMySubscription(Long userId) {

        Subscription subscription = subscriptionRepository
                .findTopByUserIdAndStatusOrderByEndAtDesc(userId, SubscriptionStatus.ACTIVE)
                .orElse(null);

        if (subscription == null) {
            return MySubscriptionResponse.builder()
                    .subscribed(false)
                    .status("NONE")
                    .remainingFreeCount(0)
                    .usedFreeCount(0)
                    .totalFreeCount(TOTAL_FREE_COUNT)
                    .autoRenew(false)
                    .nextResetAt(null)
                    .build();
        }

        int remainingFreeCount = subscription.getRemainingFreeCount();
        int usedFreeCount = TOTAL_FREE_COUNT - remainingFreeCount;

        return MySubscriptionResponse.builder()
                .subscribed(true)
                .planType(subscription.getPlanType().name())
                .planName(getPlanName(subscription.getPlanType()))
                .status(subscription.getStatus().name())
                .startAt(subscription.getStartAt())
                .endAt(subscription.getEndAt())
                .nextResetAt(subscription.getNextResetAt())
                .remainingFreeCount(remainingFreeCount)
                .usedFreeCount(usedFreeCount)
                .totalFreeCount(TOTAL_FREE_COUNT)
                .autoRenew(subscription.getAutoRenew())
                .build();
    }

    private String getPlanName(PlanType planType) {
        return switch (planType) {
            case MONTHLY -> "1개월 구독권";
            case YEARLY -> "12개월 구독권";
        };
    }
}