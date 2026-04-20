package com.wanted.naeil.domain.live.controller;

import com.wanted.naeil.domain.live.dto.request.CreateLiveLectureRequest;
import com.wanted.naeil.domain.live.dto.response.InstructorLiveDetailResponse;
import com.wanted.naeil.domain.live.service.LiveLectureService;
import com.wanted.naeil.domain.user.entity.enums.Role;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/instructor")
@Slf4j
public class InstLiveController {

    private final LiveLectureService liveLectureService;

    /**
     * 실시간 강의 신청 페이지 조회
     * @param authDetails : 사용자가 강사 or 관리자인지 검증
     * @param mv : request 값 바인딩, 및 html 페이지 설정
     * @return : liveLectureCreate.html로 이동
     */
    @GetMapping("/live-lecture")
    public ModelAndView createLiveLecture(
            @AuthenticationPrincipal AuthDetails authDetails,
            ModelAndView mv
            ) {
        // 로그인 검증
        if (authDetails == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }

        Role userRole = authDetails.getLoginUserDTO().getRole();

        // 권한 검증
        if (userRole != Role.INSTRUCTOR && userRole != Role.ADMIN) {
            throw new AccessDeniedException("관리자 혹은 강사만 생성 할 수 있습니다.");
        }

        log.info("[Live Lecture] 실시간 강의 신청 페이지 조회 시작");

        mv.addObject("user", authDetails.getLoginUserDTO());
        mv.addObject("createLiveLectureRequest",  new CreateLiveLectureRequest());
        mv.setViewName("live/liveLectureCreate");

        return mv;
    }

    /**
     * 실시간 강의 등록 form 제출 로직
     * @param authDetails : 세션에 저장된 사용자 정보
     * @param request : 실시간 강의 요청에 필요한 값들
     * @param bindingResult : dto 필수 입력값 검증 처리
     * @param mv : message, view 페이지
     * @param redirectAttributes : 리다이렉트할 때, 메시지가 잘 보이게 추가
     * @return : 성공 -> 성공 페이지, 실패 -> 값들 유지하면서 신청 폼 리다이렉트
     */
    @PostMapping("/live-lecture")
    public ModelAndView registerLiveLecture(
            @AuthenticationPrincipal AuthDetails authDetails,
            @Valid @ModelAttribute CreateLiveLectureRequest request,
            BindingResult bindingResult,
            ModelAndView mv,
            RedirectAttributes redirectAttributes
    ) {

        if (authDetails == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }

        Long instructorId = authDetails.getLoginUserDTO().getUserId();

        log.info("[LiveLecture] 라이브 강의 신청 요청 - instructorId: {}, instructorName: {}, title: {}",
                instructorId,
                authDetails.getLoginUserDTO().getName(),
                request.getTitle());

        if (bindingResult.hasErrors()) {
            log.warn(" [Validation] 강의 등록 검증 실패: {}", bindingResult.getAllErrors());

            mv.addObject("user", authDetails.getLoginUserDTO());
            mv.addObject("createLiveLectureRequest", request);
            mv.setViewName("live/liveLectureCreate");
            return mv;
        }

        try {
            String message = liveLectureService.registerLiveLecture(instructorId, request);
            redirectAttributes.addFlashAttribute("message", message);
            mv.setViewName("redirect:/instructor/course/complete");
        } catch (Exception e) {
            mv.addObject("user", authDetails.getLoginUserDTO());
            mv.addObject("createLiveLectureRequest", request);
            mv.addObject("errorMessage", "실시간 강의 등록 중 오류가 발생했습니다: " + e.getMessage());
            mv.setViewName("live/liveLectureCreate");
        }

        return mv;
    }

    @GetMapping("/live-lecture/{liveId}")
    public ModelAndView getInstructorLiveLectureDetail(
            @AuthenticationPrincipal AuthDetails authDetails,
            @PathVariable Long liveId,
            ModelAndView mv
    ) {
        if (authDetails == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }

        Long instructorId = authDetails.getLoginUserDTO().getUserId();

        InstructorLiveDetailResponse response =
                liveLectureService.getInstructorLiveLectureDetail(instructorId, liveId);

        mv.addObject("user", authDetails.getLoginUserDTO());
        mv.addObject("liveCourse", response);
        // TODO : 추후 예약자 명단 페이지 추가
        mv.addObject("reservations", List.of());
        mv.setViewName("live/liveLectureDetail");

        return mv;
    }
}
