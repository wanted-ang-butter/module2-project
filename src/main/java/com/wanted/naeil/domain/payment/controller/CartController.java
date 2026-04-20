package com.wanted.naeil.domain.payment.controller;

import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import com.wanted.naeil.domain.payment.dto.response.CartPageResponse;
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
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final PaymentService paymentService;


    // 로그인 사용자 확인
    private Long getLoginUserId(AuthDetails authDetails) {
        if (authDetails == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return authDetails.getLoginUserDTO().getUserId();
    }

    // 장바구니 담기
    @PostMapping("/add/{courseId}")
    public String addCartItem(@PathVariable Long courseId,
                              @AuthenticationPrincipal AuthDetails authDetails) {

        Long loginUserId = getLoginUserId(authDetails);

        try {
            cartService.addCartItem(loginUserId, courseId);
            return "redirect:/course/" + courseId + "?cartStatus=added";
        } catch (IllegalStateException e) {
            // 강의 상세 페이지로 다시 이동
            // TODO: 프로젝트 실제 상세 경로에 맞게 바꾸면 됨 -> 추후에 확인 필요!!!
            return "redirect:/course/" + courseId + "?cartStatus=duplicate";
        }
    }

    // 장바구니 조회(장바구니 메인)
    @GetMapping
    public String getCartPage(@RequestParam(required = false) Long selectedCartItemId,
                              @AuthenticationPrincipal AuthDetails authDetails,
                              Model model) {

        Long loginUserId = getLoginUserId(authDetails);

        CartPageResponse cartPage = cartService.getCartPage(loginUserId, selectedCartItemId);
        model.addAttribute("cartPage", cartPage);

        return "payment/cart";
    }

    // 장바구니 삭제
    @DeleteMapping("/delete/{cartItemId}")
    public String deleteCartItem(@PathVariable Long cartItemId,
                                 @AuthenticationPrincipal AuthDetails authDetails) {

        Long loginUserId = getLoginUserId(authDetails);

        cartService.deleteCartItem(loginUserId, cartItemId);

        return "redirect:/cart";
    }

    // 체크된 장바구니 항목 결제
    @PostMapping("/checkout")
    public String checkoutCartItems(@RequestParam List<Long> selectedCartItemIds,
                                    @AuthenticationPrincipal AuthDetails authDetails) {

        Long loginUserId = getLoginUserId(authDetails);

        paymentService.checkoutSelectedCartItems(loginUserId, selectedCartItemIds);

        return "redirect:/my-courses";
    }
}