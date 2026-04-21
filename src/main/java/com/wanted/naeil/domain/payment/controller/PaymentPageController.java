package com.wanted.naeil.domain.payment.controller;

import com.wanted.naeil.domain.payment.service.CreditService;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentPageController {

    private final CreditService creditService;

    @GetMapping("/history")
    public String paymentHistoryPage(@AuthenticationPrincipal AuthDetails authDetails,
                                     Model model) {

        if (authDetails == null) {
            return "redirect:/auth/login";
        }

        Long userId = authDetails.getLoginUserDTO().getUserId();

        model.addAttribute("loginUser", authDetails.getLoginUserDTO());
        model.addAttribute("balance", creditService.getBalance(userId));

        return "payment/paymentHistory";
    }
}