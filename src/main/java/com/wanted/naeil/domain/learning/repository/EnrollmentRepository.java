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

    @Query("""
        select e.status
        from Enrollment e
        where e.user.id = :userId
          and e.course.id = :courseId
    """)
    Optional<EnrollmentStatus> findStatusByUserIdAndCourseId(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId
    );

    @Query("""
        select e
        from Enrollment e
        join fetch e.user u
        where e.course.id = :courseId
        order by e.createdAt desc
    """)
    List<Enrollment> findAllWithUserByCourseIdOrderByCreatedAtDesc(
            @Param("courseId") Long courseId
    );
}
