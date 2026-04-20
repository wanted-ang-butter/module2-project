package com.wanted.naeil.domain.payment.controller;

import com.wanted.naeil.domain.payment.service.CreditService;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/credit")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    // 크레딧 충전 페이지 조회
    @GetMapping("/charge")
    public String getChargePage(@AuthenticationPrincipal AuthDetails authDetails,
                                @RequestParam(value = "success", required = false) String success,
                                Model model) {

        if (authDetails == null) {
            return "redirect:/login";
        }

        Long userId = authDetails.getLoginUserDTO().getUserId();
        int balance = creditService.getBalance(userId);

        model.addAttribute("balance", balance);
        model.addAttribute("success", success);

        return "payment/credit";
    }

    // 크레딧 충전하기
    @PostMapping("/charge")
    public String chargeCredit(@AuthenticationPrincipal AuthDetails authDetails,
                               @RequestParam("amount") int amount) {

        if (authDetails == null) {
            return "redirect:/login";
        }

        Long userId = authDetails.getLoginUserDTO().getUserId();
        creditService.chargeCredit(userId, amount);

        return "redirect:/credit/charge?success=true";
    }
}