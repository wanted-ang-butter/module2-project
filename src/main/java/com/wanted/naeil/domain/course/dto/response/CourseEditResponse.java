package com.wanted.naeil.domain.course.dto.response;

import com.wanted.naeil.domain.course.entity.Category;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.entity.enums.CourseStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CourseEditResponse {

    private Long courseId;
    private CourseStatus status;
    private Long categoryId;
    private String title;
    private String description;
    private int price;
    private String thumbnail;
    private List<CourseEditSectionResponse> sections;

    public static CourseEditResponse of(Course course, List<CourseEditSectionResponse> sections) {

        return CourseEditResponse.builder()
                .courseId(course.getId())
                .status(course.getStatus())
                .categoryId(course.getCategory().getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .thumbnail(course.getThumbnail())
                .sections(sections)
                .build();
    }

}
