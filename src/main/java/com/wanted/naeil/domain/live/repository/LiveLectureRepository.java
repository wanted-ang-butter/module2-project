package com.wanted.naeil.domain.live.repository;

import com.wanted.naeil.domain.live.entity.LiveLecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveLectureRepository extends JpaRepository<LiveLecture, Long> {
}
