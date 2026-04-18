package com.wanted.naeil.domain.payment.controller;

import com.wanted.naeil.domain.payment.dto.request.SubscriptionPaymentRequest;
import com.wanted.naeil.domain.payment.service.CartService;
import com.wanted.naeil.domain.payment.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final CartService cartService;
    private final PaymentService paymentService;

    // 코스 상세에서 '바로결제' -> 해당 코스를 장바구니에 담고 장바구니 페이지로 이동
    @PostMapping("/courses/direct/{courseId}")
    public String directCoursePayment(@PathVariable Long courseId, HttpSession session) {
        Long loginUserId = getLoginUserId(session);

        Long cartItemId = cartService.addOrGetCartItem(loginUserId, courseId);

        return "redirect:/cart?selectedCartItemId=" + cartItemId;
    }

    // 장바구니에서 선택한 코스 결제
    @PostMapping("/courses")
    public String checkoutSelectedCourses(@RequestParam List<Long> selectedCartItemIds,
                                          HttpSession session) {
        Long loginUserId = getLoginUserId(session);

        paymentService.checkoutSelectedCartItems(loginUserId, selectedCartItemIds);

        return "redirect:/my-courses";
    }

    // 구독권 구매
    @PostMapping("/subscriptions")
    public String subscribe(@ModelAttribute SubscriptionPaymentRequest req,
                            HttpSession session) {

        Long loginUserId = getLoginUserId(session);

        paymentService.subscribe(loginUserId, req);

        return "redirect:/my-page";
    }

    // 구독 자동결제 on/off 변경
    @PatchMapping("/subscriptions/auto-renew")
    public String updateSubscriptionAutoRenew(@RequestParam Boolean autoRenew,
                                              HttpSession session) {

        Long loginUserId = getLoginUserId(session);

        paymentService.updateSubscriptionAutoRenew(loginUserId, autoRenew);

        return "redirect:/my-page";
    }

    private Long getLoginUserId(HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return loginUserId;
    }

}