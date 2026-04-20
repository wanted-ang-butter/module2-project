package com.wanted.naeil.domain.live.controller;

import com.wanted.naeil.domain.live.dto.response.LiveLectureListResponse;
import com.wanted.naeil.domain.live.service.LiveLectureService;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/live-lecture")
@RequiredArgsConstructor
@Slf4j
public class UserLiveController {

    private final LiveLectureService liveLectureService;

    @GetMapping
    public ModelAndView liveLectureListPage(
            @AuthenticationPrincipal AuthDetails authDetails,
            ModelAndView mv
            ) {
        List<LiveLectureListResponse> sessions = liveLectureService.getLiveLectureList();

        log.info("[LiveLectureList] 사용자 실시간 강의 전체 조회 성공");

        if (authDetails != null) {
            mv.addObject("user", authDetails.getLoginUserDTO());
        }

        mv.addObject("sessions", sessions);
        mv.setViewName("live/liveLectureList");

        return mv;
    }
}
