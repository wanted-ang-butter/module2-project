package com.wanted.naeil.domain.payment.service;

import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import com.wanted.naeil.domain.learning.repository.EnrollmentRepository;
import com.wanted.naeil.domain.payment.dto.response.CartPageResponse;
import com.wanted.naeil.domain.payment.dto.response.CartPageResponse;
import com.wanted.naeil.domain.payment.entity.CartItem;
import com.wanted.naeil.domain.payment.entity.Credit;
import com.wanted.naeil.domain.payment.repository.CartItemRepository;
import com.wanted.naeil.domain.payment.repository.CreditRepository;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CreditRepository creditRepository;
    private final EnrollmentRepository enrollmentRepository;

    // 1. 장바구니 담기
    public void addCartItem(Long userId, Long courseId) {

        validateAlreadyPurchased(userId, courseId);
        validateAlreadyInCart(userId, courseId);

        User user = getUser(userId);
        Course course = getCourse(courseId);

        CartItem cartItem = new CartItem(user, course);
        cartItemRepository.save(cartItem);
    }

    // 2. 바로결제용
    public Long addOrGetCartItem(Long userId, Long courseId) {

        validateAlreadyPurchased(userId, courseId);

        Optional<CartItem> existing =
                cartItemRepository.findByUserIdAndCourseId(userId, courseId);

        if (existing.isPresent()) {
            return existing.get().getId();
        }

        User user = getUser(userId);
        Course course = getCourse(courseId);

        CartItem cartItem = new CartItem(user, course);
        return cartItemRepository.save(cartItem).getId();
    }

    // 3. 장바구니 삭제
    public void removeCartItem(Long userId, Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목이 존재하지 않습니다."));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인의 장바구니만 삭제할 수 있습니다.");
        }

        cartItemRepository.delete(cartItem);
    }

    // 4. 장바구니 조회
    public CartPageResponse getCartPage(Long userId, Long selectedCartItemId) {

        List<CartItem> cartItems = cartItemRepository.findAllByUserId(userId);

        Credit credit = creditRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("크레딧 정보가 존재하지 않습니다."));

        List<Long> selectedIds;

        if (selectedCartItemId != null) {
            selectedIds = List.of(selectedCartItemId);
        } else {
            selectedIds = cartItems.stream()
                    .map(CartItem::getId)
                    .toList();
        }

        int totalAmount = cartItems.stream()
                .filter(item -> selectedIds.contains(item.getId()))
                .mapToInt(item -> item.getCourse().getPrice())
                .sum();

        boolean canCheckout = credit.getBalance() >= totalAmount;

        return CartPageResponse.builder()
                .cartItems(cartItems)
                .selectedCartItemIds(selectedIds)
                .totalAmount(totalAmount)
                .creditBalance(credit.getBalance())
                .canCheckout(canCheckout)
                .build();
    }

    // ===== 공통 메서드 =====

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
    }

    private Course getCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강의 없음"));
    }

    private void validateAlreadyPurchased(Long userId, Long courseId) {
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new IllegalStateException("이미 구매한 강의입니다.");
        }
    }

    private void validateAlreadyInCart(Long userId, Long courseId) {
        if (cartItemRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new IllegalStateException("이미 장바구니에 담긴 강의입니다.");
        }
    }
}