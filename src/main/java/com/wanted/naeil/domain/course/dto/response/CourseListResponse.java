package com.wanted.naeil.domain.course.dto.response;


import com.wanted.naeil.domain.course.entity.Course;
import lombok.Builder;

@Builder
public class CourseListResponse {
    private final Long courseId;
    private final String thumbnail;
    private final String category;
    private final String description;
    private final String instructorName;
    // TODO : 별점 평균과, 학생 수는 추후 구현
//    private final String rating;
//    private final int studentCount;
    private final int price;

    public static CourseListResponse from(Course course, String message) {
        return new CourseListResponse(
            course.getId(),
                course.getThumbnail(),
                course.getCategory().getName(),
                course.getDescription(),
                course.getInstructor().getName(),
                // TODO : 별점 평균과, 학생 수 추후 추가하기
                course.getPrice()
        );
    }
}
