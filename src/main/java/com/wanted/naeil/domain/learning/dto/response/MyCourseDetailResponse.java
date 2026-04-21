package com.wanted.naeil.domain.learning.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyCourseDetailResponse {

    private Long courseId;
    private String thumbnail;
    private String title;
    private String instructorName;
    private String description;

    private Double coursesRate;
    private int completedCount;
    private int totalCount;

    private List<MySessionResponse> sessions;
}
