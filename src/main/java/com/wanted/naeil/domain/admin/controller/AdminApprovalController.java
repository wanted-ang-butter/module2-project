package com.wanted.naeil.domain.admin.controller;

import com.wanted.naeil.domain.admin.dto.response.ApprovalResponse;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalRequestType;
import com.wanted.naeil.domain.admin.service.AdminApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminApprovalController {

    private final AdminApprovalService adminApprovalService;

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
        model.addAttribute("approvals", adminApprovalService.getApprovals(ApprovalRequestType.SETTLEMENT_REGISTER));
        return "admin/settlement";
    }

    // 승인 목록 상태별 카운트를 model에 추가하는 공통 메서드
    private void addApprovalCounts(Model model, List<ApprovalResponse> approvals) {
        long pendingCount = approvals.stream().filter(a -> "PENDING".equals(a.getStatus().name())).count();
        long approvedCount = approvals.stream().filter(a -> "APPROVED".equals(a.getStatus().name())).count();
        long rejectedCount = approvals.stream().filter(a -> "REJECTED".equals(a.getStatus().name())).count();
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);
    }

    // 승인 처리 - 코스/강사/실시간/정산 공통, redirect 파라미터로 원래 페이지로 돌아감
    @PostMapping("/approvals/{id}/approve")
    public String approve(@PathVariable Long id, @RequestParam String redirect) {
        adminApprovalService.approve(id, null);
        return "redirect:" + redirect;
    }

    // 반려 처리 - 반려 사유(rejectReason) 함께 처리
    @PostMapping("/approvals/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam String rejectReason, @RequestParam String redirect) {
        adminApprovalService.reject(id, null, rejectReason);
        return "redirect:" + redirect;
    }
}
