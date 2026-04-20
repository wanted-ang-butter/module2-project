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
public class PaymentQueryService {

    private final PaymentItemRepository paymentItemRepository;

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
                .itemName(paymentItem.getItemName())
                .amount(amount)
                .displayAmount(getDisplayAmount(itemType, amount))
                .displayPaymentMethod(getDisplayPaymentMethod(itemType))
                .paidAt(paymentItem.getPayment().getPaidAt())
                .status(paymentItem.getPayment().getStatus().name())
                .build();
    }

    private String getDisplayAmount(PaymentItemType itemType, int amount) {
        return switch (itemType) {
            case CREDIT_CHARGE -> "+" + String.format("%,d", amount) + " 크레딧";
            case COURSE, SUBSCRIPTION -> "-" + String.format("%,d", amount) + " 크레딧";
        };
    }

    private String getDisplayPaymentMethod(PaymentItemType itemType) {
        return switch (itemType) {
            case CREDIT_CHARGE -> "-";
            case COURSE, SUBSCRIPTION -> "크레딧";
        };
    }
}