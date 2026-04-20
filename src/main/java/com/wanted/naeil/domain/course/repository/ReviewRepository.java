package com.wanted.naeil.domain.course.repository;

import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.entity.Review;
import com.wanted.naeil.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // 내 강의
    Optional<Review> findByUserAndCourse(User user, Course course);

    boolean existsByUserAndCourse(User user, Course course);
}
