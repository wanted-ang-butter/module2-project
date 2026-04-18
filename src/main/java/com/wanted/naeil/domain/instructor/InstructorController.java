package com.wanted.naeil.domain.instructor;

import com.wanted.naeil.domain.auth.model.dto.AuthDetails;
import com.wanted.naeil.domain.course.service.CourseService;
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

    private final CourseService courseService;


    @GetMapping("/dashboard")
    public ModelAndView dashboard(@AuthenticationPrincipal AuthDetails authDetails) {
        ModelAndView mv = new ModelAndView("dashboard/instructorDashboard");

        if (authDetails != null) {
            Long instructorId = authDetails.getLoginUserDTO().getUserId();

            mv.addObject("user", authDetails.getLoginUserDTO());
            mv.addObject("courses", courseService.getInstructorCourses(instructorId));
        }

        return mv;
    }
}