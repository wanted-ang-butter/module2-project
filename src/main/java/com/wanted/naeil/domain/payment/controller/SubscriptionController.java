package com.wanted.naeil.domain.payment.controller;

import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {

    @GetMapping
    public String subscriptionPage() {
        return "payment/subscription";
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam String planType,
                           @AuthenticationPrincipal AuthDetails authDetails,
                           Model model) {

        // 비회원이면 로그인 페이지로 이동
        if (authDetails == null) {
            return "redirect:/auth/login";
        }

        switch (planType) {
            case "MONTHLY" -> {
                model.addAttribute("planName", "1개월 이용권");
                model.addAttribute("price", 99000);
            }
            case "YEARLY" -> {
                model.addAttribute("planName", "12개월 이용권");
                model.addAttribute("price", 990000);
            }
            default -> {
                return "redirect:/subscription";
            }
        }

        model.addAttribute("planType", planType);

        return "payment/subscriptionCheckout";
    }
}