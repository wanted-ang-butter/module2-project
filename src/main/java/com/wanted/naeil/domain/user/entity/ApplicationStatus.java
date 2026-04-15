package com.wanted.naeil.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationStatus {
    PENDING("승인 대기"),
    APPROVED("승인 완료"),
    REJECTED("반려");

    private final String description;
}
