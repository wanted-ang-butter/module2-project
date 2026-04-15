package com.wanted.naeil.domain.payment.service;

import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import com.wanted.naeil.domain.learning.repository.EnrollmentRepository;
import com.wanted.naeil.domain.payment.dto.CartItemResponse;
import com.wanted.naeil.domain.payment.dto.CartPageResponse;
import com.wanted.naeil.domain.payment.dto.CartSummaryResponse;
import com.wanted.naeil.domain.payment.entity.CartItem;
import com.wanted.naeil.domain.payment.entity.Credit;
import com.wanted.naeil.domain.payment.repository.CartItemRepository;
import com.wanted.naeil.domain.payment.repository.CreditRepository;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CreditRepository creditRepository;

    public Long addCartItem(Long userId, Long courseId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강의입니다."));

        validateAlreadyPurchased(userId, courseId);
        validateAlreadyInCart(userId, courseId);

        CartItem cartItem = new CartItem(user, course);
        CartItem savedCartItem = cartItemRepository.save(cartItem);

        return savedCartItem.getId();
    }

    @Transactional(readOnly = true)
    public CartPageResponse getCartPage(Long userId) {

        List<CartItem> cartItems = cartItemRepository.findAllByUserId(userId);

        List<CartItemResponse> itemResponses = cartItems.stream()
                .map(cartItem -> new CartItemResponse(
                        cartItem.getId(),
                        cartItem.getCourse().getId(),
                        cartItem.getCourse().getTitle(),
                        cartItem.getCourse().getInstructor().getName(),
                        cartItem.getCourse().getPrice(),
                        cartItem.getCourse().getThumbnail()
                ))
                .toList();

        int totalRequiredCredit = cartItems.stream()
                .mapToInt(cartItem -> cartItem.getCourse().getPrice())
                .sum();

        int currentCredit = creditRepository.findById(userId)
                .map(Credit::getBalance)
                .orElse(0);

        int remainingCredit = currentCredit - totalRequiredCredit;
        boolean canPurchase = remainingCredit >= 0;

        CartSummaryResponse summary = new CartSummaryResponse(
                cartItems.size(),
                totalRequiredCredit,
                currentCredit,
                remainingCredit,
                canPurchase
        );

        return new CartPageResponse(itemResponses, summary);
    }

    public void removeCartItem(Long userId, Long cartItemId) {

        CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, userId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목이 존재하지 않습니다."));

        cartItemRepository.delete(cartItem);
    }

    private void validateAlreadyPurchased(Long userId, Long courseId) {
        boolean alreadyPurchased = enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
        if (alreadyPurchased) {
            throw new IllegalStateException("이미 구매한 강의입니다.");
        }
    }

    private void validateAlreadyInCart(Long userId, Long courseId) {
        boolean alreadyInCart = cartItemRepository.existsByUserIdAndCourseId(userId, courseId);
        if (alreadyInCart) {
            throw new IllegalStateException("이미 장바구니에 담긴 강의입니다.");
        }
    }
}