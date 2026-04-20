package com.wanted.naeil.domain.settlement.controller;

import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import com.wanted.naeil.domain.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/instructor/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    // 강사 정산 목록 조회
    @GetMapping
    public String getSettlements(@AuthenticationPrincipal AuthDetails authDetails,
                                 Model model) {

        Long instructorId = authDetails.getLoginUserDTO().getUserId();
        model.addAttribute("settlements", settlementService.getMySettlements(instructorId));

        return "instructor/settlement";
    }

    // 정산 신청
    @PostMapping("/{settlementId}/request")
    public String requestSettlement(@PathVariable Long settlementId,
                                    @AuthenticationPrincipal AuthDetails authDetails) {

        Long instructorId = authDetails.getLoginUserDTO().getUserId();
        settlementService.requestSettlement(instructorId, settlementId);

        return "redirect:/instructor/settlements";
    }

    // 정산 취소
    @PostMapping("/{settlementId}/cancel")
    public String cancelSettlement(@PathVariable Long settlementId,
                                   @AuthenticationPrincipal AuthDetails authDetails) {

        Long instructorId = authDetails.getLoginUserDTO().getUserId();
        settlementService.cancelSettlement(instructorId, settlementId);

        return "redirect:/instructor/settlements";
    }
}