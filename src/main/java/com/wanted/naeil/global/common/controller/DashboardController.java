package com.wanted.naeil.global.common.controller;

import com.wanted.naeil.domain.admin.dto.response.ApprovalResponse;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalRequestType;
import com.wanted.naeil.domain.admin.service.AdminApprovalService;
import com.wanted.naeil.domain.course.dto.response.InstructorCourseResponse;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.entity.enums.CourseStatus;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import com.wanted.naeil.domain.course.service.CourseService;
import com.wanted.naeil.domain.live.dto.response.InstructorLiveLectureResponse;
import com.wanted.naeil.domain.live.service.LiveLectureService;
import com.wanted.naeil.domain.mainpage.service.MainPageService;
import com.wanted.naeil.domain.settlement.entity.Settlement;
import com.wanted.naeil.domain.settlement.entity.enums.SettlementStatus;
import com.wanted.naeil.domain.settlement.repository.SettlementRepository;
import com.wanted.naeil.domain.settlement.service.SettlementService;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.entity.enums.Role;
import com.wanted.naeil.domain.user.entity.enums.UserStatus;
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

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private static final List<Role> MEMBER_ROLES = List.of(Role.USER, Role.SUBSCRIBER, Role.INSTRUCTOR);

    private final CourseService courseService;
    private final LiveLectureService liveLectureService;
    private final MainPageService mainPageService;
    private final SettlementService settlementService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final SettlementRepository settlementRepository;
    private final AdminApprovalService adminApprovalService;

    @GetMapping("/admin")
    public ModelAndView adminDashboard(@AuthenticationPrincipal AuthDetails authDetails) {

        log.info("[Dashboard] 관리자({}) 대시보드 접속", authDetails != null ? authDetails.getUsername() : "unknown");

        ModelAndView mv = new ModelAndView("dashboard/adminDashboard");
        List<ApprovalResponse> instructorApprovals =
                adminApprovalService.getApprovals(ApprovalRequestType.INSTRUCTOR_REGISTER);

        long totalMemberCount = userRepository.countByRoleIn(MEMBER_ROLES);
        long newMemberCount = userRepository.countByRoleInAndCreatedAtAfter(MEMBER_ROLES, LocalDateTime.now().minusDays(7));
        long activeMemberCount = userRepository.countByRoleInAndStatus(MEMBER_ROLES, UserStatus.ACTIVE);
        long inactiveMemberCount = totalMemberCount - activeMemberCount;
        long registeredInstructorCount = userRepository.countByRole(Role.INSTRUCTOR);
        long totalCourseCount = courseRepository.count();
        long activeCourseCount = courseRepository.countByStatus(CourseStatus.ACTIVE);

        List<AdminDashboardCourseItem> dashboardCourses = courseRepository.findTop2ByOrderByCreatedAtDesc().stream()
                .map(course -> new AdminDashboardCourseItem(
                        course.getId(),
                        course.getTitle(),
                        course.getInstructor().getName(),
                        courseRepository.countStudentsByCourseId(course.getId()),
                        course.getStatus().name(),
                        course.getStatus().getDescription()))
                .toList();

        List<Settlement> settlements = settlementRepository.findAll();
        YearMonth now = YearMonth.now();
        String currentMonth = now.toString();
        String previousMonth = now.minusMonths(1).toString();

        int currentMonthSales = settlements.stream()
                .filter(settlement -> currentMonth.equals(settlement.getSettlementMonth()))
                .mapToInt(Settlement::getTotalSalesAmount)
                .sum();

        int previousMonthSales = settlements.stream()
                .filter(settlement -> previousMonth.equals(settlement.getSettlementMonth()))
                .mapToInt(Settlement::getTotalSalesAmount)
                .sum();

        int currentMonthInstructorAmount = settlements.stream()
                .filter(settlement -> currentMonth.equals(settlement.getSettlementMonth()))
                .mapToInt(Settlement::getFinalAmount)
                .sum();

        int currentMonthPlatformRevenue = settlements.stream()
                .filter(settlement -> currentMonth.equals(settlement.getSettlementMonth()))
                .mapToInt(Settlement::getPlatformFee)
                .sum();

        String salesTrendLabel = buildTrendLabel(currentMonthSales, previousMonthSales);
        String nextSettlementDateLabel = now.plusMonths(1)
                .atDay(5)
                .format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"));

        if (authDetails != null) {
            mv.addObject("user", authDetails.getLoginUserDTO());
        }

        mv.addObject("pendingInstructorApprovalCount", instructorApprovals.size());
        mv.addObject("pendingInstructorApprovals", instructorApprovals.stream().limit(3).toList());
        mv.addObject("totalMemberCount", totalMemberCount);
        mv.addObject("newMemberCount", newMemberCount);
        mv.addObject("activeMemberCount", activeMemberCount);
        mv.addObject("inactiveMemberCount", inactiveMemberCount);
        mv.addObject("registeredInstructorCount", registeredInstructorCount);
        mv.addObject("totalCourseCount", totalCourseCount);
        mv.addObject("activeCourseCount", activeCourseCount);
        mv.addObject("dashboardCourses", dashboardCourses);
        mv.addObject("currentMonthSales", currentMonthSales);
        mv.addObject("currentMonthInstructorAmount", currentMonthInstructorAmount);
        mv.addObject("currentMonthPlatformRevenue", currentMonthPlatformRevenue);
        mv.addObject("salesTrendLabel", salesTrendLabel);
        mv.addObject("nextSettlementDateLabel", nextSettlementDateLabel);

        return mv;
    }

    @GetMapping("/instructor")
    public ModelAndView instructorDashboard(@AuthenticationPrincipal AuthDetails authDetails) {

        log.info("[Dashboard] 강사({}) 대시보드 접속", authDetails != null ? authDetails.getUsername() : "unknown");

        ModelAndView mv = new ModelAndView("dashboard/instructorDashboard");

        if (authDetails != null) {
            Long instructorId = authDetails.getLoginUserDTO().getUserId();
            List<InstructorCourseResponse> courses = courseService.getInstructorCourses(instructorId);
            List<InstructorLiveLectureResponse> liveCourses =
                    liveLectureService.getInstructorLiveLectures(instructorId);
            List<Settlement> settlements = settlementService.getMySettlements(instructorId);
            String currentMonth = YearMonth.now().toString();

            int currentMonthSales = settlements.stream()
                    .filter(settlement -> currentMonth.equals(settlement.getSettlementMonth()))
                    .mapToInt(Settlement::getTotalSalesAmount)
                    .sum();

            int currentMonthFee = settlements.stream()
                    .filter(settlement -> currentMonth.equals(settlement.getSettlementMonth()))
                    .mapToInt(Settlement::getPlatformFee)
                    .sum();

            int availableAmount = settlements.stream()
                    .filter(settlement -> settlement.getStatus() == SettlementStatus.READY)
                    .mapToInt(Settlement::getFinalAmount)
                    .sum();

            mv.addObject("user", authDetails.getLoginUserDTO());
            mv.addObject("courses", courses);
            mv.addObject("featuredCourse", courses.isEmpty() ? null : courses.get(0));
            mv.addObject("liveCourses", liveCourses);
            mv.addObject("nextLiveCourse", liveCourses.isEmpty() ? null : liveCourses.get(0));
            mv.addObject("currentMonthSales", currentMonthSales);
            mv.addObject("currentMonthFee", currentMonthFee);
            mv.addObject("availableAmount", availableAmount);
        }

        return mv;
    }

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
            mv.addObject("popularCourses", mainPageService.getPopularCourses(null));
            mv.addObject("newCourses", mainPageService.getNewCourses(null));
            mv.addObject("categories", mainPageService.getCategoryCourses());
        }
        return mv;
    }

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

    private String buildTrendLabel(int currentValue, int previousValue) {
        if (previousValue <= 0) {
            return "비교 데이터 없음";
        }

        int percent = (int) Math.round(((currentValue - previousValue) * 100.0) / previousValue);
        return String.format("%+d%% 전월 대비", percent);
    }

    public record AdminDashboardCourseItem(
            Long courseId,
            String title,
            String instructorName,
            long studentCount,
            String statusName,
            String statusLabel
    ) {
    }
}
