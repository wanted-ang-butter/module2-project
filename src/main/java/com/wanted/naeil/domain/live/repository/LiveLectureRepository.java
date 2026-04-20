package com.wanted.naeil.domain.live.repository;

import com.wanted.naeil.domain.live.entity.LiveLecture;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiveLectureRepository extends JpaRepository<LiveLecture, Long> {

    // 강사별 live 강의 리스트 조회
    @EntityGraph(attributePaths = {"instructor"})
    List<LiveLecture> findByInstructorIdOrderByCreatedAtDesc(Long instructorId);
}
