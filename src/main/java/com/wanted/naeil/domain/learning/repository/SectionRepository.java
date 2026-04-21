package com.wanted.naeil.domain.learning.repository;

import com.wanted.naeil.domain.course.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByCourseIdOrderBySequenceAsc(Long courseId);
}