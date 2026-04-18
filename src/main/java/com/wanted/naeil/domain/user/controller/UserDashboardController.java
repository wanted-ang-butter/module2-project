package com.wanted.naeil.domain.user.controller;

import com.wanted.naeil.domain.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserDashboardController {

    @GetMapping("/dashboard")
    public ModelAndView dashboard(@AuthenticationPrincipal AuthDetails authDetails) {
        ModelAndView mv = new ModelAndView("dashboard/userDashboard");
        if (authDetails != null) {
            mv.addObject("user", authDetails.getLoginUserDTO());
        }
        return mv;
    }
}
