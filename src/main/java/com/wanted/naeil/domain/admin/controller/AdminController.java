package com.wanted.naeil.domain.admin.controller;


import com.wanted.naeil.domain.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @GetMapping("/dashboard")
    public ModelAndView dashboard(@AuthenticationPrincipal AuthDetails authDetails) {
        // 1. 반환할 뷰의 경로를 생성자 인자로 전달
        ModelAndView mv = new ModelAndView("dashboard/adminDashboard");

        if (authDetails != null) {
            mv.addObject("user", authDetails.getLoginUserDTO());
        }

        return mv;
    }
}
