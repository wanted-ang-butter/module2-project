package com.wanted.naeil.domain.instructor;

import com.wanted.naeil.domain.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/instructor")
@RequiredArgsConstructor
public class InstructorController {

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal AuthDetails authDetails, Model model) {
        if (authDetails != null) {
            model.addAttribute("name", authDetails.getLoginUserDTO().getName());
            model.addAttribute("role", authDetails.getLoginUserDTO().getRole());
            model.addAttribute("nickname", authDetails.getLoginUserDTO().getNickname());
        }
        return "dashboard/instructorDashboard";
    }
}