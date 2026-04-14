package com.wanted.naeil.domain.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    READY("결제 전"),
    SUCCESS("결제 완료"),
    FAILED("결제 실패");

    private final String description;
}
