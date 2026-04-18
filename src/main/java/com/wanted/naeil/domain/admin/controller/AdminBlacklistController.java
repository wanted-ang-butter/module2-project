package com.wanted.naeil.domain.admin.controller;

import com.wanted.naeil.domain.admin.dto.request.BlacklistRequest;
import com.wanted.naeil.domain.admin.dto.response.BlacklistResponse;
import com.wanted.naeil.domain.admin.service.AdminBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/blacklist")
public class AdminBlacklistController {
    private final AdminBlacklistService adminBlacklistService;

    @GetMapping //→ 블랙리스트 전체 목록 가져와서 돌려줘
    public List<BlacklistResponse> getBlacklist() {
        return adminBlacklistService.getBlacklist();
    }
    @PostMapping //→ 바디에서 userId랑 reason 받아서 ban 처리해줘
    public void ban(@RequestBody BlacklistRequest request) {
    adminBlacklistService.ban(request.getUserId(),request.getReason());
    }
    @PatchMapping("/{blacklistId}/release") //  URL에서 blacklistId 받고, 바디에서 해제사유 받아서 unban 처리해줘
    public void unban(@PathVariable Long blacklistId,
    @RequestBody String releaseReason) {
        adminBlacklistService.unban(blacklistId,releaseReason);
    }
}


