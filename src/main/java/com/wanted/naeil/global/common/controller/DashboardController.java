package com.wanted.naeil.global.common.controller;

import com.wanted.naeil.domain.course.service.CourseService;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final CourseService courseService;

    @GetMapping("/admin")
    public ModelAndView adminDashboard(@AuthenticationPrincipal AuthDetails authDetails) {
        ModelAndView mv = new ModelAndView("dashboard/adminDashboard");
        if (authDetails != null) mv.addObject("user", authDetails.getLoginUserDTO());
        return mv;
    }

    @GetMapping("/instructor")
    public ModelAndView dashboard(@AuthenticationPrincipal AuthDetails authDetails) {
        ModelAndView mv = new ModelAndView("dashboard/instructorDashboard");

        if (authDetails != null) {
            Long instructorId = authDetails.getLoginUserDTO().getUserId();

            mv.addObject("user", authDetails.getLoginUserDTO());
            mv.addObject("courses", courseService.getInstructorCourses(instructorId));
        }

        return mv;
    }

    @GetMapping("/user")
    public ModelAndView userDashboard(@AuthenticationPrincipal AuthDetails authDetails) {
        ModelAndView mv = new ModelAndView("dashboard/userDashboard");
        if (authDetails != null) mv.addObject("user", authDetails.getLoginUserDTO());
        return mv;
    }

    @GetMapping("/guest")
    public String guestDashboard() {
        return "dashboard/guestDashboard";
    }


}
