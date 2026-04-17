package com.wanted.naeil.domain.course.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CourseDetailsResponse {

    private final Long courseId;
    private final String categoryName;
    private final String title;
    private final String instructorName;
    private final String thumbnail;
    private final int price;
    private final String description;
    private final double rating;
    private final long likeCount;
    private final long sectionCount;
    private final long studentCount;
    private List<SectionResponse> sections;
}
