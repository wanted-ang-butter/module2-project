package com.wanted.naeil.global.common.controller;

import com.wanted.naeil.domain.course.dto.response.CourseListResponse;
import com.wanted.naeil.domain.mainpage.dto.response.MainCategoryResponse;
import com.wanted.naeil.domain.mainpage.service.MainPageService;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import com.wanted.naeil.domain.user.entity.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@Slf4j
public class IndexController {

    @GetMapping(value = {"/", "/dashboard"})
    public String mainPage(@AuthenticationPrincipal AuthDetails authDetails) {

        // 비로그인
        if (authDetails == null) {
            log.info(" [Index] 비로그인 사용자 접속 -> landing 페이지 노출");
            return "redirect:/dashboard/guest";
        }

        // 로그인한 사용자의 권한 확인
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
