package com.wanted.naeil.domain.payment.controller;

import com.wanted.naeil.domain.payment.dto.response.PaymentHistoryResponse;
import com.wanted.naeil.domain.payment.entity.enums.PaymentItemType;
import com.wanted.naeil.domain.payment.service.PaymentHistoryService;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentHistoryController {

    private final PaymentHistoryService paymentHistoryService;

    // 결제 내역 조회
    @GetMapping
    public Page<PaymentHistoryResponse> getPaymentHistories(
            @AuthenticationPrincipal AuthDetails authDetails,
            @RequestParam(required = false) PaymentItemType itemType,
            @PageableDefault(size = 10, sort = "payment.paidAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = authDetails.getLoginUserDTO().getUserId();

        return paymentHistoryService.getPaymentHistories(userId, itemType, pageable);
    }
}