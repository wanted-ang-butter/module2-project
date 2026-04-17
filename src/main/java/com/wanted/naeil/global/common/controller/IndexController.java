package com.wanted.naeil.global.common.controller;

import com.wanted.naeil.domain.auth.model.dto.AuthDetails;
import com.wanted.naeil.domain.user.entity.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class IndexController {

    @GetMapping(value = {"/", "/main", "/dashboard"})
    public String mainPage(@AuthenticationPrincipal AuthDetails authDetails) {

        if (authDetails == null) {
            log.info(" [Index] 비로그인 사용자 접속 -> landing 페이지 노출");
            return "dashboard/guestDashboard";
        }

        // 2. 로그인한 사용자의 권한 확인
        Role role = authDetails.getRole();
        log.info(" [Index] 로그인 사용자({}) 접속 -> 권한별 대시보드 이동", authDetails.getUsername());

        // 3. 권한별 페이지 반환 (templates/dashboard/ 폴더 기준)
        return switch (role) {
            case ADMIN -> "dashboard/adminDashboard";
            case INSTRUCTOR -> "dashboard/instructorDashboard";
            case USER -> "dashboard/userDashboard";
            default -> "dashboard/guestDashboard";
        };
    }
}
