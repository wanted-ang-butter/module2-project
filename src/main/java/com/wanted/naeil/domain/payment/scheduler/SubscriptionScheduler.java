package com.wanted.naeil.domain.payment.scheduler;

import com.wanted.naeil.domain.payment.entity.Subscription;
import com.wanted.naeil.domain.payment.entity.enums.SubscriptionStatus;
import com.wanted.naeil.domain.payment.repository.SubscriptionRepository;
import com.wanted.naeil.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentService paymentService;

    // 매일 자정 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void processSubscriptions() {

        LocalDateTime now = LocalDateTime.now();

        List<Subscription> subscriptions = subscriptionRepository.findAll();

        for (Subscription subscription : subscriptions) {

            // 무료 횟수 리셋
            if (subscription.getNextResetAt() != null &&
                    subscription.getNextResetAt().isBefore(now)) {

                subscription.updateRemainingFreeCount(3);
                subscription.renew(
                        subscription.getEndAt(),
                        now.plusMonths(1)
                );
            }

            // 구독 만료 처리
            if (subscription.getEndAt().isBefore(now)) {

                if (Boolean.TRUE.equals(subscription.getAutoRenew())) {

                    // 자동결제
                    paymentService.renewSubscription(subscription);

                } else {

                    // 자동결제 OFF → 종료
                    subscription.updateStatus(SubscriptionStatus.EXPIRED);
                }
            }
        }
    }
}