package com.wanted.naeil.domain.learning.controller;

import com.wanted.naeil.domain.learning.dto.response.MyCourseResponse;
import com.wanted.naeil.domain.learning.service.MyCourseService;
import com.wanted.naeil.domain.live.dto.response.MyLiveReservationResponse;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/my-courses")
@RequiredArgsConstructor
@Slf4j
public class MyCourseController {

    private final MyCourseService myCourseService;
    private final UserRepository userRepository;

    // 내 강의 목록 조회
    @GetMapping
    public String myCoursePage(@AuthenticationPrincipal AuthDetails authDetails,
                               Model model) {

        log.info("[내 강의] 목록 조회 시작");

        User loginUser = getLoginUser(authDetails);
        model.addAttribute("user", authDetails.getLoginUserDTO());
        List<MyCourseResponse> myCourses = myCourseService.getMyCourses(loginUser);
        List<MyLiveReservationResponse> liveReservations = myCourseService.getLiveReservations(loginUser);

        double averageRate = myCourses.isEmpty() ? 0 :
                myCourses.stream()
                        .mapToDouble(MyCourseResponse::getCoursesRate)
                        .average()
                        .orElse(0);

        model.addAttribute("myCourses", myCourses);
        model.addAttribute("averageRate", (int)averageRate);
        model.addAttribute("liveReservations", liveReservations);

        log.info("[내 강의] 목록 조회 완료. 수강 강의 수: {}", myCourses.size());

        return "my-courses/myCourses";
    }

    // 로그인 유저 조회 공통 메서드
    private User getLoginUser(AuthDetails authDetails) {
        if (authDetails == null) {
            throw new NoSuchElementException("로그인이 필요합니다.");
        }
        return userRepository.findByUsername(authDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }
}
