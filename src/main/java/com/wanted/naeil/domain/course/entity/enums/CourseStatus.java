package com.wanted.naeil.domain.course.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseStatus {
    PENDING("승인 대기"),
    ACTIVE("활성화"),
    INACTIVE("비활성화"),
    REJECTED("반려");

    private final String description;
}
