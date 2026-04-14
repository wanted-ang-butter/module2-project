package com.wanted.naeil.domain.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentItemType {

    COURSE("코스"),
    SUBSCRIPTION("구독권");

    private final String description;
}