package com.wanted.naeil.domain.course.repository;

import com.wanted.naeil.domain.course.dto.response.SectionResponse;
import com.wanted.naeil.domain.course.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    // 강의 Id로 섹션 조회
    @Query("SELECT s FROM Section s WHERE s.course.id = :courseId")
    List<Section> findByCourseId(@Param("courseId") Long courseId);
}
