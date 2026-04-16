package com.wanted.naeil.domain.payment.controller;

import com.wanted.naeil.domain.payment.service.CartService;
import com.wanted.naeil.domain.payment.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final CartService cartService;
    private final PaymentService paymentService;

    @PostMapping("/courses/direct/{courseId}")
    public String directCoursePayment(@PathVariable Long courseId, HttpSession session) {
        Long loginUserId = getLoginUserId(session);

        Long cartItemId = cartService.addOrGetCartItem(loginUserId, courseId);

        return "redirect:/cart?selectedCartItemId=" + cartItemId;
    }

    @PostMapping("/courses")
    public String checkoutSelectedCourses(@RequestParam List<Long> selectedCartItemIds,
                                          HttpSession session) {
        Long loginUserId = getLoginUserId(session);

        paymentService.checkoutSelectedCartItems(loginUserId, selectedCartItemIds);

        return "redirect:/my-courses";
    }

    private Long getLoginUserId(HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return loginUserId;
    }
}