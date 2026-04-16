package com.wanted.naeil.domain.payment.controller;

import com.wanted.naeil.domain.payment.dto.response.CartPageResponse;
import com.wanted.naeil.domain.payment.service.CartService;
import com.wanted.naeil.domain.payment.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
    private Long getLoginUserId(HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return loginUserId;
    }

    // 장바구니 담기
    @PostMapping("/add/{courseId}")
    public String addCartItem(@PathVariable Long courseId, HttpSession session) {
        Long loginUserId = getLoginUserId(session);

        cartService.addCartItem(loginUserId, courseId);

        // 강의 상세 페이지로 다시 이동
        // 프로젝트 실제 상세 경로에 맞게 바꾸면 됨 -> 추후에 확인 필요!!!
        return "redirect:/courses/" + courseId;
    }

    // 장바구니 조회(장바구니 메인)
    @GetMapping
    public String getCartPage(@RequestParam(required = false) Long selectedCartItemId, HttpSession session, Model model) {

        Long loginUserId = getLoginUserId(session);

        CartPageResponse cartPage = cartService.getCartPage(loginUserId, selectedCartItemId);
        model.addAttribute("cartPage", cartPage);

        return "payment/cart";
    }

    // 장바구니 삭제 -> @deleteMapping 으로 변경 예정
    @DeleteMapping("/{cartItemId}")
    public String removeCartItem(@PathVariable Long cartItemId, HttpSession session) {
        Long loginUserId = getLoginUserId(session);

        cartService.removeCartItem(loginUserId, cartItemId);

        return "redirect:/cart";
    }

    // 체크된 장바구니 항목 결제
    @PostMapping("/checkout")
    public String checkoutCartItems(@RequestParam List<Long> selectedCartItemIds, HttpSession session) {

        Long loginUserId = getLoginUserId(session);

        paymentService.checkoutSelectedCartItems(loginUserId, selectedCartItemIds);

        return "redirect:/my-courses";
    }

}