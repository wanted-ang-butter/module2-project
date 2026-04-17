package com.wanted.naeil.domain.learning.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnrollmentStatus {

    BEFORE_START("수강 전"),
    IN_PROGRESS("수강 중"),
    COMPLETED("수강 완료");

    private final String description;
}
