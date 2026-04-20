package com.wanted.naeil.domain.payment.service;

import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.learning.entity.Enrollment;
import com.wanted.naeil.domain.learning.entity.enums.EnrollmentStatus;
import com.wanted.naeil.domain.learning.repository.EnrollmentRepository;
import com.wanted.naeil.domain.payment.dto.request.SubscriptionPaymentRequest;
import com.wanted.naeil.domain.payment.entity.CartItem;
import com.wanted.naeil.domain.payment.entity.Credit;
import com.wanted.naeil.domain.payment.entity.Payment;
import com.wanted.naeil.domain.payment.entity.PaymentItem;
import com.wanted.naeil.domain.payment.entity.Subscription;
import com.wanted.naeil.domain.payment.entity.enums.PaymentItemType;
import com.wanted.naeil.domain.payment.entity.enums.PlanType;
import com.wanted.naeil.domain.payment.entity.enums.SubscriptionStatus;
import com.wanted.naeil.domain.payment.repository.CartItemRepository;
import com.wanted.naeil.domain.payment.repository.CreditRepository;
import com.wanted.naeil.domain.payment.repository.PaymentRepository;
import com.wanted.naeil.domain.payment.repository.SubscriptionRepository;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final SubscriptionRepository subscriptionRepository;

    // 장바구니에서 선택한 코스만 결제로 이동!
    public Long checkoutSelectedCartItems(Long userId, List<Long> selectedCartItemIds) {

        // 아무것도 선택하지 않았을 때
        if (selectedCartItemIds == null || selectedCartItemIds.isEmpty()) {
            throw new IllegalArgumentException("선택된 장바구니 항목이 없습니다.");
        }

        User user = getUser(userId);
        Credit credit = getCredit(userId);

        // 선택한 장바구니 항목 조회
        List<CartItem> cartItems = cartItemRepository.findAllByIdInAndUserId(selectedCartItemIds, userId);

        // 장바구니 유효성 검증
        validateCartItems(selectedCartItemIds, cartItems);
        validateAlreadyPurchased(userId, cartItems);

        // 구독이 있으면 무료 횟수 갖고오기
        // 구독이 있으면 0으로 처리
        Subscription subscription = getActiveSubscription(userId);
        int remainingFreeCount = (subscription != null) ? subscription.getRemainingFreeCount() : 0;

        int totalAmount = calculateTotalAmount(cartItems);
        List<PaymentItem> paymentItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Course course = cartItem.getCourse();

            int price = course.getPrice();
            int itemDiscountAmount = 0;
            int itemFinalPrice = price;

            // 구독 무료 적용
            if (remainingFreeCount > 0) {
                itemDiscountAmount = price;
                itemFinalPrice = 0;
                remainingFreeCount--;
            }

            PaymentItem paymentItem = createPaymentItem(
                    course,
                    PaymentItemType.COURSE,
                    price,
                    itemDiscountAmount,
                    itemFinalPrice
            );

            paymentItems.add(paymentItem);
        }

        int discountAmount = paymentItems.stream()
                .mapToInt(PaymentItem::getDiscountAmount)
                .sum();

        int finalAmount = paymentItems.stream()
                .mapToInt(PaymentItem::getFinalPrice)
                .sum();

        validateEnoughCredit(credit, finalAmount);

        Payment payment = createPayment(user, totalAmount, discountAmount, finalAmount);

        // PaymentItem 연관관계 연결
        paymentItems.forEach(payment::addPaymentItem);

        // 수강 등록 생성
        for (CartItem cartItem : cartItems) {
            Enrollment enrollment = Enrollment.builder()
                    .user(user)
                    .course(cartItem.getCourse())
                    .status(EnrollmentStatus.BEFORE_START)
                    .coursesRate(0)
                    .build();

            enrollmentRepository.save(enrollment);
        }

        // 구독 무료 횟수 차감 반영
        if (subscription != null) {
            subscription.updateRemainingFreeCount(remainingFreeCount);
        }

        // 크레딧 차감
        credit.deduct(finalAmount);

        // 결제 성공 상태 변경
        payment.markSuccess();

        // 결제 데이터 저장
        Payment savedPayment = paymentRepository.save(payment);

        // 장바구니 정리
        cartItemRepository.deleteAll(cartItems);

        return savedPayment.getId();
    }

    // 구독권 구매
    public Long subscribe(Long userId, SubscriptionPaymentRequest req) {

        if (req.getPlanType() == null) {
            throw new IllegalArgumentException("구독 플랜 타입이 필요합니다.");
        }

        User user = getUser(userId);
        Credit credit = getCredit(userId);

        Subscription activeSubscription = getActiveSubscription(userId);
        validateNoActiveSubscription(activeSubscription);

        int totalAmount = getSubscriptionPrice(req.getPlanType());
        int discountAmount = 0;
        int finalAmount = totalAmount;

        validateEnoughCredit(credit, finalAmount);

        Payment payment = createPayment(user, totalAmount, discountAmount, finalAmount);

        PaymentItem paymentItem = createPaymentItem(
                null,
                PaymentItemType.SUBSCRIPTION,
                totalAmount,
                discountAmount,
                finalAmount
        );

        payment.addPaymentItem(paymentItem);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endAt = calculateSubscriptionEndAt(req.getPlanType(), now);
        LocalDateTime nextResetAt = now.plusMonths(1);

        Subscription subscription = Subscription.builder()
                .payment(payment)
                .user(user)
                .planType(req.getPlanType())
                .startAt(now)
                .endAt(endAt)
                .nextResetAt(nextResetAt)
                .remainingFreeCount(3)
                .autoRenew(req.getAutoRenew() != null ? req.getAutoRenew() : true)
                .status(SubscriptionStatus.ACTIVE)
                .build();

        credit.deduct(finalAmount);
        payment.markSuccess();

        Payment savedPayment = paymentRepository.save(payment);
        subscriptionRepository.save(subscription);

        return savedPayment.getId();
    }

    // 자동결제 on/off 변경
    public void updateSubscriptionAutoRenew(Long userId, Boolean autoRenew) {

        if (autoRenew == null) {
            throw new IllegalArgumentException("자동결제 설정값이 필요합니다.");
        }

        Subscription subscription = getActiveSubscription(userId);

        if (subscription == null || subscription.getEndAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("현재 이용 중인 구독이 없습니다.");
        }

        subscription.updateAutoRenew(autoRenew);
    }

    private void validateCartItems(List<Long> selectedCartItemIds, List<CartItem> cartItems) {
        if (cartItems.size() != selectedCartItemIds.size()) {
            throw new IllegalArgumentException("유효하지 않은 장바구니 항목이 포함되어 있습니다.");
        }
    }

    // 이미 수강 등록된 코스가 장바구니에 담겨있고, 다른 코스와 함께 결제하려 할 때
    private void validateAlreadyPurchased(Long userId, List<CartItem> cartItems) {
        boolean alreadyPurchased = cartItems.stream()
                .anyMatch(cartItem ->
                        enrollmentRepository.existsByUserIdAndCourseId(userId, cartItem.getCourse().getId())
                );

        if (alreadyPurchased) {
            throw new IllegalStateException("이미 구매한 강의가 포함되어 있습니다.");
        }
    }

    // 총 결제 금액 계산(선택 강의 가격의 합으로 필요 크레딧을 계산)
    private int calculateTotalAmount(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToInt(cartItem -> cartItem.getCourse().getPrice())
                .sum();
    }

    // 크레딧 > 강의 가격일 경우 결제 가능
    // 크레딧 < 강의 가격일 경우 결제 불가 -> 일 때
    private void validateEnoughCredit(Credit credit, int finalAmount) {
        if (credit.getBalance() < finalAmount) {
            throw new IllegalStateException("크레딧이 부족합니다.");
        }
    }

    // 구독 플랜 타입(가격)
    private int getSubscriptionPrice(PlanType planType) {
        return switch (planType) {
            case MONTHLY -> 99000;
            case YEARLY -> 990000;
        };
    }

    // 구독 플랜 타입(기간)
    private LocalDateTime calculateSubscriptionEndAt(PlanType planType, LocalDateTime now) {
        return switch (planType) {
            case MONTHLY -> now.plusMonths(1);
            case YEARLY -> now.plusYears(1);
        };
    }

    // 구독 활성화
    private Subscription getActiveSubscription(Long userId) {
        return subscriptionRepository
                .findTopByUserIdAndStatusOrderByEndAtDesc(userId, SubscriptionStatus.ACTIVE)
                .orElse(null);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    private Credit getCredit(Long userId) {
        return creditRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("크레딧 정보가 존재하지 않습니다."));
    }

    private void validateNoActiveSubscription(Subscription subscription) {
        if (subscription != null && subscription.getEndAt().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("이미 이용 중인 구독이 존재합니다.");
        }
    }

    // 자동결제
    public void renewSubscription(Subscription subscription) {

        User user = subscription.getUser();

        Credit credit = creditRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("크레딧 정보 없음"));

        int amount = getSubscriptionPrice(subscription.getPlanType());

        if (credit.getBalance() < amount) {
            subscription.updateStatus(SubscriptionStatus.EXPIRED);
            return;
        }

        Payment payment = createPayment(user, amount, 0, amount);

        PaymentItem paymentItem = createPaymentItem(
                null,
                PaymentItemType.SUBSCRIPTION,
                amount,
                0,
                amount
        );

        payment.addPaymentItem(paymentItem);

        credit.deduct(amount);
        payment.markSuccess();

        paymentRepository.save(payment);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newEndAt = calculateSubscriptionEndAt(subscription.getPlanType(), now);
        LocalDateTime newNextResetAt = now.plusMonths(1);

        subscription.renew(newEndAt, newNextResetAt);
        subscription.updateRemainingFreeCount(3);
    }

    private Payment createPayment(User user, int totalAmount, int discountAmount, int finalAmount) {
        return Payment.builder()
                .user(user)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .build();
    }

    private PaymentItem createPaymentItem(Course course,
                                          PaymentItemType itemType,
                                          int price,
                                          int discountAmount,
                                          int finalPrice) {
        return PaymentItem.builder()
                .course(course)
                .itemType(itemType)
                .price(price)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .build();
    }
}