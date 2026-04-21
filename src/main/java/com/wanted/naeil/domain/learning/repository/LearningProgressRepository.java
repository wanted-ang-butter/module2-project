package com.wanted.naeil.domain.learning.repository;

import com.wanted.naeil.domain.learning.entity.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningProgressRepository extends JpaRepository<LearningProgress, Long> {

    Optional<LearningProgress> findByUserIdAndSectionId(Long userId, Long sectionId);

    List<LearningProgress> findByUserIdAndSectionCourseId(Long userId, Long courseId);
}
