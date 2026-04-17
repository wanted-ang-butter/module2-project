package com.wanted.naeil.domain.community.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LikeTargetType {
    POST("게시글"),
    COURSE("코스");

    private final String description;
}
