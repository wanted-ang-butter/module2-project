package com.wanted.naeil.domain.user.controller;


import com.wanted.naeil.domain.auth.model.dto.AuthDetails;
import com.wanted.naeil.domain.user.dto.SignupDTO;
import com.wanted.naeil.domain.user.service.MemberService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView signup(@Valid @ModelAttribute SignupDTO signupDTO, BindingResult bindingResult, ModelAndView mv) {



// [형식 검사] @Pattern 등에 걸리면 여기로 들어옵니다.
        if (bindingResult.hasErrors()) {
            // 에러 메시지 중 첫 번째 것을 가져와서 화면에 뿌려줍니다.
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            mv.addObject("message", errorMessage);
            mv.setViewName("guest/signup");
            return mv;
        }


        // 여기서 발생하는 중복 아이디, 이미지 형식 에러 등은 GlobalExceptionHandler가 처리
        memberService.regist(signupDTO);

        // 3. 성공 시 처리
        mv.addObject("message", "회원가입이 완료되었습니다.");
        mv.setViewName("auth/login");
        return mv;
    }

    // 역할 별 대시보드
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal AuthDetails authDetails, Model model) {
        if (authDetails != null) {
            model.addAttribute("name", authDetails.getLoginUserDTO().getName());
            model.addAttribute("role", authDetails.getLoginUserDTO().getRole());
            model.addAttribute("nickname", authDetails.getLoginUserDTO().getNickname());
        }
        return "dashboard/userDashboard";
    }



}