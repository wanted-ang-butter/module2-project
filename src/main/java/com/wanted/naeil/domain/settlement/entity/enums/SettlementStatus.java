package com.wanted.naeil.domain.settlement.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementStatus {

    PENDING("승인 대기"),
    APPROVED("승인 완료"),
    REJECTED("반려"),
    CANCELED("강사 취소");

    private final String description;
}
