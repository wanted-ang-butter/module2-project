package com.wanted.naeil.domain.course.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseSortType {

    LATEST("최신순"),
    OLDEST("오래된순"),
    POPULAR("인기순");

    private final String description;

    public static CourseSortType from(String value) {
        if (value == null || value.isBlank()) {
            return LATEST;
        }

        try {
            return CourseSortType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return LATEST;
        }
    }
}
