package com.wanted.naeil.domain.admin.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprovalStatus {
    PENDING("승인 대기"),
    APPROVED("승인 완료"),
    REJECTED("반려");

    private final String description;
}
