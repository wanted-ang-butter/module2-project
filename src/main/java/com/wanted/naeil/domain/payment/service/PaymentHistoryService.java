package com.wanted.naeil.domain.payment.service;

import com.wanted.naeil.domain.payment.dto.response.PaymentHistoryResponse;
import com.wanted.naeil.domain.payment.entity.PaymentItem;
import com.wanted.naeil.domain.payment.entity.enums.PaymentItemType;
import com.wanted.naeil.domain.payment.repository.PaymentItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentHistoryService {

    private final PaymentItemRepository paymentItemRepository;

    // 결제 내역 조회
    public Page<PaymentHistoryResponse> getPaymentHistories(Long userId,
                                                            PaymentItemType itemType,
                                                            Pageable pageable) {

        Page<PaymentItem> paymentItems = (itemType == null)
                ? paymentItemRepository.findByPayment_User_Id(userId, pageable)
                : paymentItemRepository.findByPayment_User_IdAndItemType(userId, itemType, pageable);

        return paymentItems.map(this::toResponse);
    }

    private PaymentHistoryResponse toResponse(PaymentItem paymentItem) {
        PaymentItemType itemType = paymentItem.getItemType();
        int amount = paymentItem.getFinalPrice();

        return PaymentHistoryResponse.builder()
                .paymentId(paymentItem.getPayment().getId())
                .itemType(itemType.name())
                .itemTypeName(itemType.getDescription())
                .itemName(getItemName(paymentItem))
                .amount(amount)
                .displayAmount(getDisplayAmount(itemType, amount))
                .displayPaymentMethod(getDisplayPaymentMethod(itemType))
                .paidAt(paymentItem.getPayment().getPaidAt())
                .status(paymentItem.getPayment().getStatus().name())
                .build();
    }

    private String getItemName(PaymentItem paymentItem) {
        return switch (paymentItem.getItemType()) {
            case COURSE -> {
                if (paymentItem.getCourse() == null) {
                    yield "코스";
                }
                yield paymentItem.getCourse().getTitle();
            }
            case SUBSCRIPTION -> "구독권";
            case CREDIT -> String.format("%,d 크레딧 충전", paymentItem.getFinalPrice());
        };
    }

    private String getDisplayAmount(PaymentItemType itemType, int amount) {
        return switch (itemType) {
            case CREDIT -> "+" + String.format("%,d", amount) + " 크레딧";
            case COURSE, SUBSCRIPTION -> "-" + String.format("%,d", amount) + " 크레딧";
        };
    }

    private String getDisplayPaymentMethod(PaymentItemType itemType) {
        return switch (itemType) {
            case CREDIT -> "-";
            case COURSE, SUBSCRIPTION -> "크레딧";
        };
    }
}