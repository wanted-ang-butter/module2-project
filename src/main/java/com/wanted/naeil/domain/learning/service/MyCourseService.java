package com.wanted.naeil.domain.learning.service;

import com.wanted.naeil.domain.learning.dto.response.MyCourseResponse;
import com.wanted.naeil.domain.course.entity.Review;
import com.wanted.naeil.domain.course.repository.ReviewRepository;
import com.wanted.naeil.domain.learning.entity.Enrollment;
import com.wanted.naeil.domain.learning.repository.EnrollmentRepository;
import com.wanted.naeil.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyCourseService {

    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;

    // 내 강의 목록 조회
    @Transactional(readOnly = true)
    public List<MyCourseResponse> getMyCourses(User loginUser) {

        List<Enrollment> enrollments = enrollmentRepository.findByUserWithCourse(loginUser);
        return enrollments.stream()
                .map(enrollment -> {
                    Optional<Review> review = reviewRepository.findByUserAndCourse(
                            loginUser,
                            enrollment.getCourse()
                    );
                    return MyCourseResponse.builder()
                            .courseId(enrollment.getCourse().getId())
                            .thumbnail(enrollment.getCourse().getThumbnail())
                            .title(enrollment.getCourse().getTitle())
                            .instructorName(enrollment.getCourse().getInstructor().getName())
                            .coursesRate(enrollment.getCoursesRate())
                            .reviewId(review.map(Review::getId).orElse(null))
                            .rating(review.map(Review::getRating).orElse(null))
                            .reviewContent(review.map(Review::getContent).orElse(null))
                            .build();
                })
                .toList();
    }
}
