package com.wanted.naeil.domain.admin.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprovalRequestType {
    COURSE_REGISTER("코스 등록 요청"),
    COURSE_DELETE("코스 삭제 요청"),
    LIVE_REGISTER("라이브 등록 요청"),
    INSTRUCTOR_REGISTER("강사 등록 요청"),
    SETTLEMENT_REGISTER("정산 요청");

    private final String description;
}
