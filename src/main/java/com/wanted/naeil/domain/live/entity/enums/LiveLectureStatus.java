package com.wanted.naeil.domain.live.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LiveLectureStatus {
    PENDING("승인 대기"),
    APPROVED("승인 완료"),
    REJECTED("반려됨"),
    IN_PROGRESS("방송 중"),
    ENDED("종료됨"),
    CANCELLED("요청 취소");

    private final String description;
}
