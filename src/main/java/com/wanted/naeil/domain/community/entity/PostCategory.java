package com.wanted.naeil.domain.community.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostCategory {
    FREE("자유"), QNA("QnA");

    private final String description;
}
