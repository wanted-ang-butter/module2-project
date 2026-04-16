package com.wanted.naeil.domain.user;

import com.wanted.naeil.domain.user.TestLoginRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

// 이 클래스는 테스트용으로 테스트 완료 시 삭제 예정!!
@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/login")
    public String testLogin(@RequestBody TestLoginRequest req, HttpSession session) {
        session.setAttribute("loginUserId", req.getUserId());
        return "test login success : " + session.getAttribute("loginUserId");
    }

    @GetMapping("/me")
    public String checkLogin(HttpSession session) {
        return "현재 로그인 userId = " + session.getAttribute("loginUserId");
    }
}