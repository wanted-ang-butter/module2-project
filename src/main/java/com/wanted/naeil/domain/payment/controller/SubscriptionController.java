package com.wanted.naeil.domain.payment.controller;

import com.wanted.naeil.domain.payment.dto.request.SubscriptionPaymentRequest;
import com.wanted.naeil.domain.payment.service.CreditService;
import com.wanted.naeil.domain.payment.service.PaymentService;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subscription")
public class SubscriptionController {

    private final PaymentService paymentService;
    private final CreditService creditService;

    @GetMapping
    public String subscriptionPage() {
        return "payment/subscription";
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam String planType,
                           @AuthenticationPrincipal AuthDetails authDetails,
                           Model model) {

        if (authDetails == null) {
            return "redirect:/auth/login";
        }

        int requiredCredit;

        switch (planType) {
            case "MONTHLY" -> {
                model.addAttribute("planName", "1개월 이용권");
                model.addAttribute("price", 99000);
                requiredCredit = 99000;
            }
            case "YEARLY" -> {
                model.addAttribute("planName", "12개월 이용권");
                model.addAttribute("price", 990000);
                requiredCredit = 990000;
            }
            default -> {
                return "redirect:/subscription";
            }
        }

        Long userId = authDetails.getLoginUserDTO().getUserId();
        int balance = creditService.getBalance(userId);
        boolean hasEnoughCredit = balance >= requiredCredit;

        model.addAttribute("planType", planType);
        model.addAttribute("loginUser", authDetails.getLoginUserDTO());
        model.addAttribute("balance", balance);
        model.addAttribute("hasEnoughCredit", hasEnoughCredit);
        model.addAttribute("lackCreditAmount", Math.max(0, requiredCredit - balance));

        return "payment/subscriptionCheckout";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> subscribe(@AuthenticationPrincipal AuthDetails authDetails,
                                       @RequestBody SubscriptionPaymentRequest request) {

        if (authDetails == null) {
            return ResponseEntity.badRequest().body("로그인이 필요합니다.");
        }

        try {
            Long userId = authDetails.getLoginUserDTO().getUserId();
            Long paymentId = paymentService.subscribe(userId, request);
            return ResponseEntity.ok(paymentId);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("결제 처리 중 오류가 발생했습니다.");
        }
    }
}