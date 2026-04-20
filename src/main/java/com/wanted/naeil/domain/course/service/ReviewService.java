package com.wanted.naeil.domain.course.service;

import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.entity.Review;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import com.wanted.naeil.domain.course.repository.ReviewRepository;
import com.wanted.naeil.domain.learning.repository.EnrollmentRepository;
import com.wanted.naeil.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    // 수강평 작성
    @Transactional
    public void createReview(Long courseId, Double rating, String content, User loginUser) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 강의입니다."));

        if (!enrollmentRepository.existsByUserIdAndCourseId(loginUser.getId(), courseId)) {
            throw new IllegalArgumentException("수강 중인 강의에만 수강평을 작성할 수 있습니다.");
        }

        if (reviewRepository.existsByUserAndCourse(loginUser, course)) {
            throw new IllegalStateException("이미 수강평을 작성했습니다.");
        }

        Review review = Review.builder()
                .course(course)
                .user(loginUser)
                .rating(rating)
                .content(content)
                .build();

        reviewRepository.save(review);
    }

    // 수강평 수정
    @Transactional
    public void updateReview(Long reviewId, Double rating, String content, User loginUser) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 수강평입니다."));

        if (!review.getUser().getId().equals(loginUser.getId())) {
            throw new IllegalArgumentException("본인의 수강평만 수정할 수 있습니다.");
        }

        review.update(rating, content);
    }

    // 수강평 삭제
    @Transactional
    public void deleteReview(Long reviewId, User loginUser) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 수강평입니다."));

        if (!review.getUser().getId().equals(loginUser.getId())) {
            throw new IllegalArgumentException("본인의 수강평만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }
}
