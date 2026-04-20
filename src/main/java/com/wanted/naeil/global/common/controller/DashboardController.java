package com.wanted.naeil.global.common.controller;

import com.wanted.naeil.domain.course.service.CourseService;
import com.wanted.naeil.domain.mainpage.service.MainPageService;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final CourseService courseService;
    private final MainPageService mainPageService;
    private final UserRepository userRepository;

    // 관리자 대시보드
    @GetMapping("/admin")
    public ModelAndView adminDashboard(@AuthenticationPrincipal AuthDetails authDetails) {

        log.info("[Dashboard] 관리자({}) 대시보드 접속", authDetails != null ? authDetails.getUsername() : "unknown");

        ModelAndView mv = new ModelAndView("dashboard/adminDashboard");
        if (authDetails != null) mv.addObject("user", authDetails.getLoginUserDTO());
        return mv;
    }

    // 강사 대시보드
    @GetMapping("/instructor")
    public ModelAndView instructorDashboard(@AuthenticationPrincipal AuthDetails authDetails) {

        log.info("[Dashboard] 강사({}) 대시보드 접속", authDetails != null ? authDetails.getUsername() : "unknown");

        ModelAndView mv = new ModelAndView("dashboard/instructorDashboard");

        if (authDetails != null) {
            Long instructorId = authDetails.getLoginUserDTO().getUserId();

            mv.addObject("user", authDetails.getLoginUserDTO());
            mv.addObject("courses", courseService.getInstructorCourses(instructorId));
        }

        return mv;
    }

    // 학생 대시보드
    @GetMapping("/user")
    public ModelAndView userDashboard(@AuthenticationPrincipal AuthDetails authDetails) {

        log.info("[Dashboard] 학생({}) 대시보드 접속", authDetails != null ? authDetails.getUsername() : "unknown");
        ModelAndView mv = new ModelAndView("dashboard/userDashboard");
        if (authDetails != null) {
            User loginUser = userRepository.findByUsername(authDetails.getUsername())
                            .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));
            mv.addObject("user", loginUser);
            mv.addObject("enrolledCount", mainPageService.getEnrolledCount(loginUser));
            mv.addObject("averageProgress", mainPageService.getAverageProgress(loginUser));
            mv.addObject("recommendedCourses", mainPageService.getRecommendedCourses(loginUser));
        }
        return mv;
    }

    // 비로그인 대시보드
    @GetMapping("/guest")
    public ModelAndView guestDashboard(@RequestParam(required = false) String category) {

        log.info("[Dashboard] 비로그인 사용자 guest 대시보드 접속");
        ModelAndView mv = new ModelAndView("dashboard/guestDashboard");
        mv.addObject("popularCourses", mainPageService.getPopularCourses(category));
        mv.addObject("newCourses", mainPageService.getNewCourses(category));
        mv.addObject("categories", mainPageService.getCategoryCourses());
        mv.addObject("selectedCategory", category);
        return mv;
    }
}
