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
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    private final MainPageService mainPageService;
    private final UserRepository userRepository;

    @GetMapping(value = {"/", "/dashboard"})
    public String mainPage(@AuthenticationPrincipal AuthDetails authDetails,
                                 @RequestParam(required = false) String category,
                                 Model model) {

        // 공통 데이터 조회 (비로그인/로그인)
        List<CourseListResponse> popularCourses = mainPageService.getPopularCourses(category);
        List<CourseListResponse> newCourses = mainPageService.getNewCourses(category);
        List<MainCategoryResponse> categories = mainPageService.getCategoryCourses();

        model.addAttribute("popularCourses", popularCourses);
        model.addAttribute("newCourses", newCourses);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);

        // 비로그인
        if (authDetails == null) {
            log.info(" [Index] 비로그인 사용자 접속 -> landing 페이지 노출");
            return ("dashboard/guestDashboard");
        }

        // 로그인 유저 조회
        User loginUser = userRepository.findByUsername(authDetails.getLoginUserDTO().getUsername())
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));

        // 로그인한 사용자의 권한 확인
        Role role = authDetails.getRole();
        log.info(" [Index] 로그인 사용자({}) 접속 -> 권한별 대시보드 이동", authDetails.getUsername());

        // 로그인 유저 추가 데이터
        model.addAttribute("user", loginUser);
        model.addAttribute("enrolledCount", mainPageService.getEnrolledCount(loginUser));
        model.addAttribute("averageProgress", mainPageService.getAverageProgress(loginUser));
        model.addAttribute("recommendedCourses", mainPageService.getRecommendedCourses(loginUser));

        // 3. 권한별 뷰 반환 (templates/dashboard/ 폴더 기준)
        return switch (role)  {
            case ADMIN -> "dashboard/adminDashboard";
            case INSTRUCTOR -> "dashboard/instructorDashboard";
            case USER -> "dashboard/userDashboard";
            default -> "dashboard/guestDashboard";
        };
    }
}
