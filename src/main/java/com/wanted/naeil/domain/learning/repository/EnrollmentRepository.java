package com.wanted.naeil.domain.learning.repository;

import com.wanted.naeil.domain.learning.entity.Enrollment;
import com.wanted.naeil.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    List<Enrollment> findByUser(User user);
}