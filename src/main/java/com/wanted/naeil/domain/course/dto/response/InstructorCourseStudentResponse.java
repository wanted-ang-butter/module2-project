package com.wanted.naeil.domain.course.dto.response;

import com.wanted.naeil.domain.learning.entity.Enrollment;
import com.wanted.naeil.domain.learning.entity.enums.EnrollmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InstructorCourseStudentResponse {

    private final Long userId;
    private final String name;
    private final String nickname;
    private final String email;
    private final String phone;
    private final String profileImg;
    private final String enrollmentStatusLabel;
    private final int progressRate;
    private final LocalDateTime enrolledAt;

    public static InstructorCourseStudentResponse from(Enrollment enrollment) {
        return InstructorCourseStudentResponse.builder()
                .userId(enrollment.getUser().getId())
                .name(enrollment.getUser().getName())
                .nickname(enrollment.getUser().getNickname())
                .email(enrollment.getUser().getEmail())
                .phone(enrollment.getUser().getPhone())
                .profileImg(enrollment.getUser().getProfileImg())
                .enrollmentStatusLabel(toStatusLabel(enrollment.getStatus()))
                .progressRate((int) Math.round(enrollment.getCoursesRate()))
                .enrolledAt(enrollment.getCreatedAt())
                .build();
    }

    public String getEnrolledDateLabel() {
        return enrolledAt == null ? "-" : enrolledAt.toLocalDate().toString();
    }

    public String getProgressRateLabel() {
        return progressRate + "%";
    }

    private static String toStatusLabel(EnrollmentStatus status) {
        if (status == null) {
            return "-";
        }

        return switch (status) {
            case BEFORE_START -> "수강 전";
            case IN_PROGRESS -> "수강 중";
            case COMPLETED -> "수강 완료";
        };
    }
}
