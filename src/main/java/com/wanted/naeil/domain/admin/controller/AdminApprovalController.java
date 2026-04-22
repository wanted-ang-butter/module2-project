package com.wanted.naeil.domain.admin.controller;

import com.wanted.naeil.domain.admin.dto.response.ApprovalResponse;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalRequestType;
import com.wanted.naeil.domain.admin.service.AdminApprovalService;
import com.wanted.naeil.domain.settlement.service.SettlementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminApprovalController {

    private final AdminApprovalService adminApprovalService;
    private final SettlementService settlementService;

    // 승인 목록 상태별 카운트를 model에 추가하는 공통 메서드
    private void addApprovalCounts(Model model, List<ApprovalResponse> approvals) {
        long pendingCount = approvals.stream().filter(a -> "PENDING".equals(a.getStatus().name())).count();
        long approvedCount = approvals.stream().filter(a -> "APPROVED".equals(a.getStatus().name())).count();
        long rejectedCount = approvals.stream().filter(a -> "REJECTED".equals(a.getStatus().name())).count();
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);
    }

    // 코스 승인 페이지 - COURSE_REGISTER 타입 승인 목록 + 상태별 카운트
    @GetMapping("/course-management/approval")
    public String courseApproval(Model model) {
        List<ApprovalResponse> approvals = adminApprovalService.getApprovals(ApprovalRequestType.COURSE_REGISTER);
        addApprovalCounts(model, approvals);
        model.addAttribute("approvals", approvals);
        return "admin/AdminApproval";
    }

    // 강사 승인 페이지 - INSTRUCTOR_REGISTER 타입 승인 목록
    @GetMapping("/instructor-approval")
    public String instructorApproval(Model model) {
        model.addAttribute("approvals", adminApprovalService.getApprovals(ApprovalRequestType.INSTRUCTOR_REGISTER));
        return "admin/instructor-application";
    }

    // 실시간 강의 승인 페이지 - LIVE_REGISTER 타입 승인 목록 + 상태별 카운트
    @GetMapping("/live-management")
    public String liveApproval(Model model) {
        List<ApprovalResponse> approvals = adminApprovalService.getApprovals(ApprovalRequestType.LIVE_REGISTER);
        addApprovalCounts(model, approvals);
        model.addAttribute("approvals", approvals);
        return "admin/live-approval";
    }

    // 정산 관리 페이지 - SETTLEMENT_REGISTER 타입 승인 목록
    @GetMapping("/settlement")
    public String settlement(Model model) {
        settlementService.syncPendingSettlementApprovals();
        List<ApprovalResponse> approvals = adminApprovalService.getApprovals(ApprovalRequestType.SETTLEMENT_REGISTER);
        long pendingCount = approvals.stream().filter(a -> "PENDING".equals(a.getStatus().name())).count();
        int totalPlatformFee = approvals.stream().mapToInt(a -> a.getPlatformFee() != null ? a.getPlatformFee() : 0).sum();
        model.addAttribute("approvals", approvals);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("totalPlatformFee", totalPlatformFee);
        return "admin/settlement";
    }

    // 승인 처리 - 코스/강사/실시간/정산 공통, redirect 파라미터로 원래 페이지로 돌아감
    @PostMapping("/approvals/{id}/approve")
    public String approve(@PathVariable Long id,
                          @RequestParam(required = false) String redirect,
                          HttpServletRequest request) {
        adminApprovalService.approve(id, null);
        return resolveRedirect(redirect, request);
    }

    // 반려 처리 - 반려 사유(rejectReason) 함께 처리
    @PostMapping("/approvals/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam(required = false) String rejectReason,
                         @RequestParam(required = false) String redirect,
                         HttpServletRequest request) {
        adminApprovalService.reject(id, null, normalizeRejectReason(rejectReason));
        return resolveRedirect(redirect, request);
    }

    private String resolveRedirect(String redirect, HttpServletRequest request) {
        if (StringUtils.hasText(redirect) && redirect.startsWith("/")) {
            return "redirect:" + redirect;
        }

        String referer = request.getHeader("Referer");
        if (StringUtils.hasText(referer)) {
            try {
                URI refererUri = URI.create(referer);
                String path = refererUri.getPath();
                String query = refererUri.getQuery();

                if (StringUtils.hasText(path) && path.startsWith("/")) {
                    return "redirect:" + path + (query != null ? "?" + query : "");
                }
            } catch (IllegalArgumentException ignored) {
                // Ignore malformed referer values and fall back to the default admin page.
            }
        }

        return "redirect:/admin/course-management/approval";
    }

    private String normalizeRejectReason(String rejectReason) {
        if (StringUtils.hasText(rejectReason)) {
            return rejectReason;
        }
        return "관리자 반려";
    }
}
