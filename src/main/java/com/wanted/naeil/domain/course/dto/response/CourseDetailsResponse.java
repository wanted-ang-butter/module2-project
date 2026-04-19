package com.wanted.naeil.domain.course.dto.response;

import com.wanted.naeil.domain.course.entity.Course;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CourseDetailsResponse {

    private final Long courseId;
    private final String category;
    private final String title;
    private final String instructorName;
    private final String thumbnail;
    private final int price;
    private final String description;

    private final String rating;
    private final long likeCount;
    private final long sectionCount;
    private final long studentCount;

    private List<SectionListResponse> sections;

    public static CourseDetailsResponse of(Course course, long likeCount, long studentCount,
                                           Double avgRating, List<SectionListResponse> sections
    ) {
        return CourseDetailsResponse.builder()
                .courseId(course.getId())
                .category(course.getCategory().getName())
                .title(course.getTitle())
                .instructorName(course.getInstructor().getName())
                .thumbnail(course.getThumbnail())
                .price(course.getPrice())
                .description(course.getDescription())
                .rating(avgRating != null ? String.format("%.1f", avgRating) : "0.0")
                .likeCount(likeCount)
                .sectionCount(sections != null ? sections.size() : 0)
                .studentCount(studentCount)
                .sections(sections)
                .build();
    }
}
