package com.wanted.naeil.domain.user.controller;


import com.wanted.naeil.domain.user.dto.request.FindIdRequest;
import com.wanted.naeil.domain.user.dto.request.FindPasswordRequest;
import com.wanted.naeil.domain.user.dto.response.FindIdResponse;
import com.wanted.naeil.domain.user.dto.request.SignupDTO;
import com.wanted.naeil.domain.user.dto.response.FindPasswordResponse;
import com.wanted.naeil.domain.user.service.MemberService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    @PostConstruct
    public void init() {
        System.out.println("🔥 UserController 로딩됨");
    }

    private final MemberService memberService;

    // 회원가입 매핑
    @GetMapping("/signup")
    public String signupPage() {

        return "guest/signup"; // templates/user/signup.html 을 찾아감

    }

    // 로그인 매핑
    @GetMapping("/login")
    public void login(){
    }


    @PostMapping("/signup")
    public ModelAndView signup(@Valid @ModelAttribute("signupDTO") SignupDTO signupDTO,
                               BindingResult bindingResult,
                               ModelAndView mv) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            mv.addObject("message", errorMessage);
            mv.addObject("signupDTO", signupDTO);
            mv.setViewName("guest/signup");
            return mv;
        }

        try {
            memberService.regist(signupDTO);
            mv.addObject("message", "회원가입이 완료되었습니다.");
            mv.setViewName("auth/login");
        } catch (DuplicateKeyException | IllegalArgumentException e) {
            mv.addObject("message", e.getMessage());
            mv.addObject("signupDTO", signupDTO);
            mv.setViewName("guest/signup");
        }

        return mv;
    }

    @GetMapping("/find-id")
    public String findIdPage() {
        return "auth/findId";
    }

    @PostMapping("/find-id")
    public ModelAndView findId(@ModelAttribute FindIdRequest request, ModelAndView mv) {
        try {
            FindIdResponse response = memberService.findUsernameByEmailAndPhone(request);
            mv.addObject("result", response);
        } catch (NoSuchElementException e) {
            mv.addObject("errorMessage", e.getMessage());
        }
        mv.setViewName("auth/findId");
        return mv;
    }

    @GetMapping("/find-password")
    public String findPasswordPage() {
        return "auth/findPassword";
    }

    @PostMapping("/find-password")
    public ModelAndView findPassword(@ModelAttribute FindPasswordRequest request, ModelAndView mv) {
        try {
            FindPasswordResponse response = memberService.resetPassword(request);
            mv.addObject("result", response);
        } catch (NoSuchElementException e) {
            mv.addObject("errorMessage", e.getMessage());
        }
        mv.setViewName("auth/findPassword");
        return mv;
    }




}