package com.wanted.naeil.domain.settlement.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementStatus {

    READY("신청 가능"),
    PENDING("승인 대기"), // 강사 신청 완료, 관리자 처리 대기
    APPROVED("승인 완료"),
    REJECTED("반려"),
    CANCELED("강사 취소");

    private final String description;
}
