package com.wanted.naeil.domain.course.dto.response;

import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.entity.enums.CourseStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstructorCourseResponse {

    private Long courseId;
    private String thumbnail;
    private String category;
    private CourseStatus status;
    private String title;
    private long studentCount;
    private String rating;

    public static InstructorCourseResponse of(Course course, long studentCount, Double avgRating) {

        return InstructorCourseResponse.builder()
                .courseId(course.getId())
                .thumbnail(course.getThumbnail())
                .category(course.getCategory().getName())
                .status(course.getStatus())
                .title(course.getTitle())
                .studentCount(studentCount)
                .rating(avgRating != null ? String.format("%.1f", avgRating) : "0.0")
                .build();
    }

}
