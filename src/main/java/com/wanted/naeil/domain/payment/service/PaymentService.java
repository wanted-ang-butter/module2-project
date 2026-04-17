package com.wanted.naeil.domain.payment.service;

import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.learning.entity.Enrollment;
import com.wanted.naeil.domain.learning.entity.enums.EnrollmentStatus;
import com.wanted.naeil.domain.learning.repository.EnrollmentRepository;
import com.wanted.naeil.domain.payment.entity.CartItem;
import com.wanted.naeil.domain.payment.entity.Credit;
import com.wanted.naeil.domain.payment.entity.Payment;
import com.wanted.naeil.domain.payment.entity.PaymentItem;
import com.wanted.naeil.domain.payment.entity.enums.PaymentItemType;
import com.wanted.naeil.domain.payment.repository.CartItemRepository;
import com.wanted.naeil.domain.payment.repository.CreditRepository;
import com.wanted.naeil.domain.payment.repository.PaymentRepository;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final UserRepository userRepository;
    private final CreditRepository creditRepository;
    private final CartItemRepository cartItemRepository;
    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public Long checkoutSelectedCartItems(Long userId, List<Long> selectedCartItemIds) {

        if (selectedCartItemIds == null || selectedCartItemIds.isEmpty()) {
            throw new IllegalArgumentException("선택된 장바구니 항목이 없습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Credit credit = creditRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("크레딧 정보가 존재하지 않습니다."));

        List<CartItem> cartItems = cartItemRepository.findAllByIdInAndUserId(selectedCartItemIds, userId);

        validateCartItems(selectedCartItemIds, cartItems);
        validateAlreadyPurchased(userId, cartItems);

        int totalAmount = calculateTotalAmount(cartItems);
        int discountAmount = 0;
        int finalAmount = totalAmount;

        validateEnoughCredit(credit, finalAmount);

        Payment payment = Payment.builder()
                .user(user)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .build();

        for (CartItem cartItem : cartItems) {
            Course course = cartItem.getCourse();

            PaymentItem paymentItem = PaymentItem.builder()
                    .course(course)
                    .itemType(PaymentItemType.COURSE)
                    .price(course.getPrice())
                    .discountAmount(0)
                    .finalPrice(course.getPrice())
                    .build();

            payment.addPaymentItem(paymentItem);

            Enrollment enrollment = Enrollment.builder()
                    .user(user)
                    .course(course)
                    .status(EnrollmentStatus.BEFORE_START)
                    .coursesRate(0)
                    .build();

            enrollmentRepository.save(enrollment);
        }

        credit.deduct(finalAmount);
        payment.markSuccess();

        Payment savedPayment = paymentRepository.save(payment);
        cartItemRepository.deleteAll(cartItems);

        return savedPayment.getId();
    }

    private void validateCartItems(List<Long> selectedCartItemIds, List<CartItem> cartItems) {
        if (cartItems.size() != selectedCartItemIds.size()) {
            throw new IllegalArgumentException("유효하지 않은 장바구니 항목이 포함되어 있습니다.");
        }
    }

    private void validateAlreadyPurchased(Long userId, List<CartItem> cartItems) {
        boolean alreadyPurchased = cartItems.stream()
                .anyMatch(cartItem ->
                        enrollmentRepository.existsByUserIdAndCourseId(userId, cartItem.getCourse().getId())
                );

        if (alreadyPurchased) {
            throw new IllegalStateException("이미 구매한 강의가 포함되어 있습니다.");
        }
    }

    private int calculateTotalAmount(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToInt(cartItem -> cartItem.getCourse().getPrice())
                .sum();
    }

    private void validateEnoughCredit(Credit credit, int finalAmount) {
        if (credit.getBalance() < finalAmount) {
            throw new IllegalStateException("크레딧이 부족합니다.");
        }
    }
}