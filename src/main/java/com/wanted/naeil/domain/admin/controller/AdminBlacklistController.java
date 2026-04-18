package com.wanted.naeil.domain.admin.controller;

import com.wanted.naeil.domain.admin.service.AdminBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminBlacklistController {

private final AdminBlacklistService adminBlacklistService;

    // 블랙리스트 해제
    @PostMapping("/blacklist/{id}/release")
    public String release(@PathVariable Long id) {
        adminBlacklistService.unban(id, "관리자 해제");
        return "redirect:/admin/blacklist";
    }
    // 블랙리스트 페이지 - 해제되지 않은 블랙리스트 목록 조회
    @GetMapping("/blacklist")
    public String blacklist(Model model) {
        model.addAttribute("blacklist", adminBlacklistService.getBlacklist());
        return "admin/blacklist";
    }

}
