package com.wanted.naeil.domain.payment.controller;

import com.wanted.naeil.domain.payment.dto.response.MySubscriptionResponse;
import com.wanted.naeil.domain.payment.service.MySubscriptionService;
import com.wanted.naeil.domain.payment.service.PaymentService;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class MySubscriptionController {

    private final MySubscriptionService mySubscriptionService;
    private final PaymentService paymentService;

    // 나의 구독 정보 조회
    @GetMapping
    public MySubscriptionResponse getMySubscription(
            @AuthenticationPrincipal AuthDetails authDetails
    ) {
        Long userId = authDetails.getLoginUserDTO().getUserId();
        return mySubscriptionService.getMySubscription(userId);
    }


     // 구독 해지 (자동결제 해제)
    @PatchMapping("/cancel")
    public String cancelSubscription(
            @AuthenticationPrincipal AuthDetails authDetails
    ) {
        Long userId = authDetails.getLoginUserDTO().getUserId();

        // 해지 = 자동결제 false 처리
        paymentService.updateSubscriptionAutoRenew(userId, false);
        return "구독이 해지되었습니다.";
    }
}