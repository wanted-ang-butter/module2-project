package com.wanted.naeil.domain.user.controller;

import com.wanted.naeil.domain.course.repository.CategoryRepository;
import com.wanted.naeil.domain.user.dto.request.*;
import com.wanted.naeil.domain.user.dto.response.AccountSettingResponse;
import com.wanted.naeil.domain.user.dto.response.InstructorApplicationResponse;
import com.wanted.naeil.domain.user.entity.enums.ApplicationStatus;
import com.wanted.naeil.domain.user.service.InstructorApplicationService;
import com.wanted.naeil.domain.user.service.MemberService;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final InstructorApplicationService instructorApplicationService;
    private final CategoryRepository categoryRepository;

    // 커밋시점 잡기
    @GetMapping("/me")
    public ModelAndView myPage(@AuthenticationPrincipal AuthDetails authDetails, ModelAndView mv) {
        AccountSettingResponse myPage = memberService.getMyPage(authDetails.getUsername());
        mv.addObject("myPage", myPage);
        mv.setViewName("mypage/accountSettings");
        return mv;
    }

    @PostMapping("/me/profile-image")
    public String updateProfileImg(@AuthenticationPrincipal AuthDetails authDetails,
                                   @ModelAttribute UpdateProfileImgRequest request) {
        memberService.updateProfileImg(authDetails.getUsername(), request);
        return "redirect:/user/me";
    }

    @PostMapping("/me/profile-image/delete")
    public String deleteProfileImg(@AuthenticationPrincipal AuthDetails authDetails) {
        memberService.deleteProfileImg(authDetails.getUsername());
        return "redirect:/user/me";
    }

    @PostMapping("/me/nickname")
    public String updateNickname(@AuthenticationPrincipal AuthDetails authDetails,
                                 @Valid @ModelAttribute UpdateNicknameRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            return "redirect:/user/me";
        }
        try {
            memberService.updateNickname(authDetails.getUsername(), request);
            redirectAttributes.addFlashAttribute("successMessage", "닉네임이 변경되었습니다.");
        } catch (DuplicateKeyException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/me";
    }


    // 비밀번호 변경(완료 시 세션 만료)
    @PostMapping("/me/password")
    public String updatePassword(@AuthenticationPrincipal AuthDetails authDetails,
                                 @Valid @ModelAttribute UpdatePasswordRequest request,
                                 BindingResult bindingResult,
                                 HttpServletRequest httpRequest,
                                 HttpServletResponse httpResponse,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            return "redirect:/user/me";
        }
        try {
            memberService.updatePassword(authDetails.getUsername(), request);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(httpRequest, httpResponse, auth);
            }
            redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다. 다시 로그인해 주세요.");
            return "redirect:/auth/login";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/me";
        }
    }

    @PostMapping("/me/email")
    public String updateEmail(@AuthenticationPrincipal AuthDetails authDetails,
                              @Valid @ModelAttribute UpdateEmailRequest request,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            return "redirect:/user/me";
        }
        try {
            memberService.updateEmail(authDetails.getUsername(), request);
            redirectAttributes.addFlashAttribute("successMessage", "이메일이 변경되었습니다.");
        } catch (DuplicateKeyException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/me";
    }

    // 회원탈퇴
    @PostMapping("/me/withdraw")
    public String withdraw(@AuthenticationPrincipal AuthDetails authDetails,
                           @ModelAttribute WithdrawRequest request,
                           HttpServletRequest httpRequest,
                           HttpServletResponse httpResponse,
                           RedirectAttributes redirectAttributes) {
        try {
            // 1. DB 상의 상태 변경 (Soft Delete)
            memberService.withdraw(authDetails.getUsername(), request);

            // 2. 실제 로그아웃 처리 (세션 및 시큐리티 컨텍스트 클리어)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(httpRequest, httpResponse, auth);
            }

            redirectAttributes.addFlashAttribute("message", "탈퇴가 완료되었습니다. 그동안 이용해주셔서 감사합니다.");
            return "redirect:/"; // 메인으로 리다이렉트

        } catch (IllegalArgumentException e) {
            // 비밀번호가 틀린 경우 등
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/me"; // 마이페이지(혹은 설정페이지)로 복귀
        }
    }

    // 강사 신청 내역 조회
    @GetMapping("/me/instructor-application/history")
    public ModelAndView instructorApplicationHistory(@AuthenticationPrincipal AuthDetails authDetails, ModelAndView mv) {
        List<InstructorApplicationResponse> applications =
                instructorApplicationService.getMyApplications(authDetails.getUsername());
        mv.addObject("user", authDetails.getLoginUserDTO());
        mv.addObject("applications", applications);
        mv.addObject("hasPending", applications.stream()
                .anyMatch(a -> a.getStatus() == ApplicationStatus.PENDING));
        mv.setViewName("mypage/instructorApplicationHistory");
        return mv;
    }

    // 강사 신청 폼
    @GetMapping("/me/apply-instructor")
    public ModelAndView applyInstructor(@AuthenticationPrincipal AuthDetails authDetails, ModelAndView mv) {
        mv.addObject("user", authDetails.getLoginUserDTO());
        mv.addObject("categories", categoryRepository.findAll());
        mv.setViewName("mypage/InstructorApplication"); // 수정
        return mv;
    }

    // 강사 신청 제출
    @PostMapping("/me/apply-instructor")
    public String applyInstructor(@AuthenticationPrincipal AuthDetails authDetails,
                                  @ModelAttribute ApplyInstructorRequest request,
                                  RedirectAttributes redirectAttributes) {
        try {
            instructorApplicationService.apply(authDetails.getUsername(), request);
            redirectAttributes.addFlashAttribute("successMessage", "강사 신청이 완료되었습니다.");
            return "redirect:/user/me/instructor-application/history";
        } catch (IllegalStateException | IllegalArgumentException e) { // IllegalArgumentException 추가
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/me/apply-instructor";
        }
    }

    // 강사 신청 철회
    @PostMapping("/me/instructor-application/{id}/cancel")
    public String cancelApplication(@AuthenticationPrincipal AuthDetails authDetails,
                                    @PathVariable Long id,
                                    RedirectAttributes redirectAttributes) {
        try {
            instructorApplicationService.cancel(authDetails.getUsername(), id);
            redirectAttributes.addFlashAttribute("successMessage", "신청이 철회되었습니다.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/me/instructor-application/history";
    }
}
