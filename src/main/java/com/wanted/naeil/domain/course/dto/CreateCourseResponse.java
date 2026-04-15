package com.wanted.naeil.domain.course.dto;

import com.wanted.naeil.domain.course.entity.Course;
import lombok.Builder;

@Builder
public record CreateCourseResponse(
    Long id, // 코스id
    String message
) {

    public static CreateCourseResponse from(Course course, String message) {
        return CreateCourseResponse.builder()
                .id(course.getId())
                .message(message)
                .build();
    }
}
