package com.wanted.naeil.domain.payment.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionStatus {

    ACTIVE("사용 중"),
    EXPIRED("만료"),
    CANCELED("취소");

    private final String description;
}