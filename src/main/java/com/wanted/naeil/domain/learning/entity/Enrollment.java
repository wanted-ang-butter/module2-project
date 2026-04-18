package com.wanted.naeil.domain.learning.entity;

import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.learning.entity.enums.EnrollmentStatus;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_enrollment_user_course",
                        columnNames = {"user_id", "course_id"}
                )
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnrollmentStatus status;

    @Column(name = "courses_rate", nullable = false)
    private double coursesRate;

    @Builder
    public Enrollment(User user, Course course, EnrollmentStatus status, double coursesRate) {
        this.user = user;
        this.course = course;
        this.status = status;
        this.coursesRate = coursesRate;
    }

    // 진도율 업데이트
    public void updateProgress(double rate) {
        this.coursesRate = rate;
    }

    // 상태 변경
    public void updateStatus(EnrollmentStatus status) {
        this.status = status;
    }
}
