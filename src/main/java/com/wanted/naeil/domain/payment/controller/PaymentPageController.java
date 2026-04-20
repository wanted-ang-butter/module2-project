package com.wanted.naeil.domain.payment.controller;

import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payment")
public class PaymentPageController {

    @GetMapping("/history")
    public String paymentHistoryPage(@AuthenticationPrincipal AuthDetails authDetails,
                                     Model model) {

        model.addAttribute("loginUser", authDetails.getLoginUserDTO());

        return "payment/paymentHistory";
    }
}