package com.wanted.naeil.domain.settlement.controller;

import com.wanted.naeil.domain.settlement.entity.Settlement;
import com.wanted.naeil.domain.settlement.entity.enums.SettlementStatus;
import com.wanted.naeil.domain.settlement.service.SettlementService;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/instructor/settlements")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN', 'INSTRUCTOR')")
public class SettlementController {

    private final SettlementService settlementService;

    // 강사 정산 목록 조회
    @GetMapping
    public String getSettlements(@AuthenticationPrincipal AuthDetails authDetails,
                                 Model model) {

        Long instructorId = authDetails.getLoginUserDTO().getUserId();
        List<Settlement> settlements = settlementService.getMySettlements(instructorId);
        String currentMonth = YearMonth.now().toString();

        int currentMonthSales = settlements.stream()
                .filter(settlement -> currentMonth.equals(settlement.getSettlementMonth()))
                .mapToInt(Settlement::getTotalSalesAmount)
                .sum();

        int currentMonthFee = settlements.stream()
                .filter(settlement -> currentMonth.equals(settlement.getSettlementMonth()))
                .mapToInt(Settlement::getPlatformFee)
                .sum();

        int availableAmount = settlements.stream()
                .filter(settlement -> settlement.getStatus() == SettlementStatus.READY)
                .mapToInt(Settlement::getFinalAmount)
                .sum();

        int totalCompletedAmount = settlements.stream()
                .filter(settlement -> settlement.getStatus() == SettlementStatus.APPROVED)
                .mapToInt(Settlement::getFinalAmount)
                .sum();

        model.addAttribute("settlements", settlements);
        model.addAttribute("currentMonthSales", currentMonthSales);
        model.addAttribute("currentMonthFee", currentMonthFee);
        model.addAttribute("availableAmount", availableAmount);
        model.addAttribute("totalCompletedAmount", totalCompletedAmount);

        return "instructor/settlement";
    }

    // 정산 생성 후 신청 -> 채린
    @PostMapping("/request")
    public String createAndRequestSettlement(@AuthenticationPrincipal AuthDetails authDetails) {

        Long instructorId = authDetails.getLoginUserDTO().getUserId();
        settlementService.createAndRequestSettlement(instructorId, YearMonth.now());

        return "redirect:/instructor/settlements";
    }

    // 기존 정산 신청
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