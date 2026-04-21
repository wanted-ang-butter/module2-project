package com.wanted.naeil.domain.learning.dto.response;

import com.wanted.naeil.domain.learning.entity.enums.EnrollmentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyCourseResponse {

    // 강의 기본 정보
    private Long courseId;
    private String thumbnail;
    private String title;
    private String instructorName;

    // 수강 정보
    private Double coursesRate;

    // 수강평 정보
    private Long reviewId;
    private Double rating;
    private String reviewContent;

    private EnrollmentStatus enrollmentStatus;
    private Long firstSectionId;
}
