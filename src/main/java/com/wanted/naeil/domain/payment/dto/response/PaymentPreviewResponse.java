package com.wanted.naeil.domain.payment.dto.response;

import com.wanted.naeil.domain.payment.entity.PaymentItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PaymentPreviewResponse {

    private List<PaymentItem> paymentItems;
    private List<PaymentPreviewItemResponse> items;
    private int totalAmount;
    private int discountAmount;
    private int finalAmount;
    private int myCredit;
    private int remainingCredit;
    private boolean canPay;
    private boolean isSubscriber;
}
