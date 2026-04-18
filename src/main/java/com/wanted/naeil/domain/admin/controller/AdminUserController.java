package com.wanted.naeil.domain.admin.controller;

import com.wanted.naeil.domain.admin.dto.response.UserResponse;
import com.wanted.naeil.domain.admin.service.AdminBlacklistService;
import com.wanted.naeil.domain.admin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final AdminBlacklistService adminBlacklistService;

    // 회원 목록 페이지 - USER/INSTRUCTOR 전체 조회 + 상태별 카운트
    @GetMapping("/user-management")
    public String userList(Model model) {
        List<UserResponse> users = adminUserService.getUser();
        long activeCount = users.stream().filter(u -> "ACTIVE".equals(u.getStatus().name())).count();
        long inactiveCount = users.stream().filter(u -> !"ACTIVE".equals(u.getStatus().name())).count();
        model.addAttribute("users", users);
        model.addAttribute("totalCount", users.size());
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("inactiveCount", inactiveCount);
        return "admin/user-list";
    }

    // 회원 활성화
    @PostMapping("/user-management/{id}/activate")
    public String activateUser(@PathVariable Long id) {
        adminUserService.activateUser(id);
        return "redirect:/admin/user-management";
    }

    // 회원 비활성화
    @PostMapping("/user-management/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id) {
        adminUserService.deactivateUser(id);
        return "redirect:/admin/user-management";
    }

    // 경고 횟수 초기화
    @PostMapping("/user-management/{id}/reset-warning")
    public String resetWarning(@PathVariable Long id) {
        adminUserService.resetWarnigUser(id);
        return "redirect:/admin/user-management";
    }

    // 회원 삭제 - 비활성 상태인 회원만 삭제 가능
    @PostMapping("/user-management/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return "redirect:/admin/user-management";
    }

    // 회원 블랙리스트 - 등록은 사유 함께 저장
    @PostMapping("/user-management/{id}/blacklist")
    public String blacklistUser(@PathVariable Long id, @RequestParam String reason) {
        adminBlacklistService.ban(id, reason);
        return "redirect:/admin/user-management";
    }
}
