package com.wanted.naeil.domain.payment.controller;

import com.wanted.naeil.domain.payment.dto.response.MySubscriptionResponse;
import com.wanted.naeil.domain.payment.service.MySubscriptionService;
import com.wanted.naeil.domain.payment.service.PaymentService;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class MySubscriptionController {

    private final MySubscriptionService mySubscriptionService;
    private final PaymentService paymentService;

    // 나의 구독 정보 조회
    @GetMapping
    public ResponseEntity<MySubscriptionResponse> getMySubscription(
            @AuthenticationPrincipal AuthDetails authDetails
    ) {
        Long userId = getUserId(authDetails);
        return ResponseEntity.ok(mySubscriptionService.getMySubscription(userId));
    }

    // 구독 해지 (자동결제 해제)
    @PatchMapping("/cancel")
    public ResponseEntity<Map<String, String>> cancelSubscription(
            @AuthenticationPrincipal AuthDetails authDetails
    ) {
        Long userId = getUserId(authDetails);

        // 해지 = 자동결제 false 처리
        paymentService.updateSubscriptionAutoRenew(userId, false);

        return ResponseEntity.ok(Map.of("message", "구독이 해지되었습니다."));
    }

    private Long getUserId(AuthDetails authDetails) {
        if (authDetails == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return authDetails.getLoginUserDTO().getUserId();
    }
}