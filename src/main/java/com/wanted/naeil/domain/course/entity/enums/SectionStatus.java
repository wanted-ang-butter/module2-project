package com.wanted.naeil.domain.course.entity.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SectionStatus {
    ACTIVE("활성화"),
    INACTIVE("비활성화");

    private final String description;

}
