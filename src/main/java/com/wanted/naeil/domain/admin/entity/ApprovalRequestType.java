package com.wanted.naeil.domain.admin.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprovalRequestType {
    REGISTER("등록 요청"),
    DELETE("삭제 요청");

    private final String description;
}
