package com.wanted.naeil.domain.payment.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlanType {

    MONTHLY("한달권"),
    YEARLY("일년권");

    private final String description;
}