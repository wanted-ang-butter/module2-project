package com.wanted.naeil.domain.course.dto.response;


import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.entity.CourseStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CourseListResponse {

    private final Long courseId;
    private final String thumbnail;
    private final String category;
    private final String title;
    private final String description;
    private final String instructorName;
    private final String rating;
    private final long studentCount;
    private final int price;

    public CourseListResponse(Long courseId, String thumbnail, String category,
                              String title, String description, String instructorName,
                              Double avgRating, Long studentCount, int price) {
        this.courseId = courseId;
        this.thumbnail = thumbnail;
        this.category = category;
        this.title = title;

        String firstLine = (description != null) ? description.split("\n")[0] : "";
        this.description = firstLine.length() > 30 ? firstLine.substring(0, 30) + "..." : firstLine;

        this.instructorName = instructorName;
        this.rating = (avgRating != null) ? String.format("%.1f", avgRating) : "0.0";
        this.studentCount = studentCount != null ? studentCount : (int)0;
        this.price = price;
    }
}
