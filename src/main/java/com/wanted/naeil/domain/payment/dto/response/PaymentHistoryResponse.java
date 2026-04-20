package com.wanted.naeil.domain.payment.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentHistoryResponse {

    private Long paymentId;
    private String itemType;
    private String itemTypeName;
    private String itemName;
    private Integer amount;
    private String displayAmount;
    private String displayPaymentMethod;
    private LocalDateTime paidAt;
    private String status;
}