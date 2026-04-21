package com.wanted.naeil.domain.payment.controller;

import com.wanted.naeil.domain.payment.dto.response.PaymentPreviewResponse;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import com.wanted.naeil.domain.payment.dto.request.SubscriptionPaymentRequest;
import com.wanted.naeil.domain.payment.service.CartService;
import com.wanted.naeil.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final CartService cartService;
    private final PaymentService paymentService;

    private Long getLoginUserId(AuthDetails authDetails) {
        if (authDetails == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return authDetails.getLoginUserDTO().getUserId();
    }

    // 코스 상세에서 '바로결제' -> 해당 코스를 장바구니에 담고 장바구니 페이지로 이동
    @PostMapping("/courses/direct/{courseId}")
    public String directCoursePayment(@PathVariable Long courseId,
                                      @AuthenticationPrincipal AuthDetails authDetails) {

        Long loginUserId = getLoginUserId(authDetails);

        Long cartItemId = cartService.addOrGetCartItem(loginUserId, courseId);

        return "redirect:/cart?selectedCartItemId=" + cartItemId;
    }

    // 장바구니에서 선택한 코스 결제
    @PostMapping("/courses")
    public String checkoutSelectedCourses(@RequestParam List<Long> selectedCartItemIds,
                                          @AuthenticationPrincipal AuthDetails authDetails) {

        Long loginUserId = getLoginUserId(authDetails);

        paymentService.checkoutSelectedCartItems(loginUserId, selectedCartItemIds);

        return "redirect:/my-courses";
    }

    @GetMapping("/courses")
    public String paymentPage(@RequestParam List<Long> selectedCartItemIds,
                              @AuthenticationPrincipal AuthDetails authDetails,
                              Model model) {

        Long loginUserId = getLoginUserId(authDetails);

        PaymentPreviewResponse preview =
                paymentService.getPaymentPreview(loginUserId, selectedCartItemIds);

        model.addAttribute("preview", preview);
        model.addAttribute("selectedCartItemIds", selectedCartItemIds);

        return "payment/payment";
    }

    // 구독권 구매
    @PostMapping("/subscriptions")
    public String subscribe(@ModelAttribute SubscriptionPaymentRequest req,
                            @AuthenticationPrincipal AuthDetails authDetails) {

        Long loginUserId = getLoginUserId(authDetails);

        paymentService.subscribe(loginUserId, req);

        return "redirect:/my-page";
    }

    // 구독 자동결제 on/off 변경
    @PatchMapping("/subscriptions/auto-renew")
    public String updateSubscriptionAutoRenew(@RequestParam Boolean autoRenew,
                                              @AuthenticationPrincipal AuthDetails authDetails) {

        Long loginUserId = getLoginUserId(authDetails);

        paymentService.updateSubscriptionAutoRenew(loginUserId, autoRenew);

        return "redirect:/my-page";
    }
}