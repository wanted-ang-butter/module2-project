package com.wanted.naeil.domain.admin.controller;

import com.wanted.naeil.domain.admin.dto.request.RejectRequest;
import com.wanted.naeil.domain.admin.dto.response.ApprovalResponse;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalRequestType;
import com.wanted.naeil.domain.admin.service.AdminApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/course-approvals")
public class AdminApprovalController {
    private final AdminApprovalService adminApprovalService;

    @GetMapping
    public List<ApprovalResponse> getApprovals (@RequestParam ApprovalRequestType type) {
        return adminApprovalService.getApprovals(type);
    }
    @PatchMapping("/{approvalId}/approve")
    public void approve(@PathVariable Long approvalId) {
        adminApprovalService.approve(approvalId, null);
        // TODO: 세션 구현 후 null 자리에 로그인한 관리자 User 객체 넣기

    }@PatchMapping("/{approvalId}/reject")
    public void reject(@PathVariable Long approvalId , @RequestBody RejectRequest request) {
       adminApprovalService.reject(approvalId, null , request.getRejectReason());
        // TODO: 세션 구현 후 null 자리에 로그인한 관리자 User 객체 넣기
    }









}