package com.wanted.naeil.domain.learning.repository;

import com.wanted.naeil.domain.learning.entity.Enrollment;
import com.wanted.naeil.domain.learning.entity.enums.EnrollmentStatus;
import com.wanted.naeil.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    List<Enrollment> findByUser(User user);

    // 내 강의 페이지용
    @Query("""
        SELECT e FROM Enrollment e
        JOIN FETCH e.course c
        JOIN FETCH c.instructor
        WHERE e.user = :user
        ORDER BY e.createdAt DESC
    """)
    List<Enrollment> findByUserWithCourse(@Param("user") User user);

    @Query("""
        select e.status
        from Enrollment e
        where e.user.id = :userId
          and e.course.id = :courseId
    """)
    Optional<EnrollmentStatus> findStatusByUserIdAndCourseId(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId);
}