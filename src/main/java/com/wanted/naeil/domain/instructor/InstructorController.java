package com.wanted.naeil.domain.instructor;

import com.wanted.naeil.domain.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/instructor")
@RequiredArgsConstructor
public class InstructorController {


    @GetMapping("/dashboard")
    public ModelAndView dashboard(@AuthenticationPrincipal AuthDetails authDetails) {
        ModelAndView mv = new ModelAndView("dashboard/instructorDashboard");

        if (authDetails != null) {
            // 개별 필드가 아닌 LoginUserDTO 객체 자체를 "user"라는 이름으로 넘김
            mv.addObject("user", authDetails.getLoginUserDTO());
        }

        return mv;
    }
}