package com.wanted.naeil.global.common.controller;

import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import com.wanted.naeil.domain.user.entity.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class IndexController {

    @GetMapping(value = {"/", "/dashboard"})
    public String mainPage(@AuthenticationPrincipal AuthDetails authDetails) {

        if (authDetails == null) {
            log.info(" [Index] 비로그인 사용자 접속 -> landing 페이지 노출");
            return "redirect:/dashboard/guest";
        }

        Role role = authDetails.getRole();
        log.info(" [Index] 로그인 사용자({}) 접속 -> 권한별 대시보드 리다이렉트", authDetails.getUsername());

        return switch (role) {
            case ADMIN -> "redirect:/dashboard/admin";
            case INSTRUCTOR -> "redirect:/dashboard/instructor";
            case USER -> "redirect:/dashboard/user";
            default -> "redirect:/dashboard/guest";
        };
    }
}
