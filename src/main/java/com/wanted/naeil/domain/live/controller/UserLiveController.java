package com.wanted.naeil.domain.live.controller;

import com.wanted.naeil.domain.live.dto.response.LiveLectureListResponse;
import com.wanted.naeil.domain.live.service.LiveLectureService;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/live-lecture")
@RequiredArgsConstructor
@Slf4j
public class UserLiveController {

    private final LiveLectureService liveLectureService;

    // 실시간 강의 전체 조회
    @GetMapping
    public ModelAndView liveLectureListPage(
            @AuthenticationPrincipal AuthDetails authDetails,
            ModelAndView mv
            ) {
        Long userId = null;

        if (authDetails != null) {
            userId = authDetails.getLoginUserDTO().getUserId();
            mv.addObject("user", authDetails.getLoginUserDTO());
        }

        List<LiveLectureListResponse> sessions = liveLectureService.getLiveLectureList(userId);

        log.info("[LiveLectureList] 사용자 실시간 강의 전체 조회 성공");

        mv.addObject("sessions", sessions);
        mv.setViewName("live/liveLectureList");

        return mv;
    }

    // 실시가 강의 에약
    @PostMapping("/{liveId}/reservations")
    public String reserveLiveLecture(
            @AuthenticationPrincipal AuthDetails authDetails,
            @PathVariable Long liveId,
            RedirectAttributes  redirectAttributes
    ) {

        if (authDetails == null) {
            throw new AccessDeniedException("로그인 후 실시간 강의를 예약할 수 있습니다.");
        }

        Long userId = authDetails.getLoginUserDTO().getUserId();

        try {
            liveLectureService.reserveLiveLecture(userId, liveId);
            redirectAttributes.addFlashAttribute("message", "실시간 강의 예약이 완료되었습니다");
            // TODO : 현지랑 합병 후, 내 강의 페이지로 이동시켜주기
//            return "redirect:/my-courses";
            return "redirect:/live-lecture/reservations/complete";
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/live-lecture";
        }

    }
}
