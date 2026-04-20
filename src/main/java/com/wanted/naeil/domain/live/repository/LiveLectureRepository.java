package com.wanted.naeil.domain.live.repository;

import com.wanted.naeil.domain.live.entity.LiveLecture;
import com.wanted.naeil.domain.live.entity.enums.LiveLectureStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LiveLectureRepository extends JpaRepository<LiveLecture, Long> {

    // 강사별 live 강의 리스트 조회
    @EntityGraph(attributePaths = {"instructor"})
    List<LiveLecture> findByInstructorIdOrderByCreatedAtDesc(Long instructorId);

    // 종료 전 강의들만 보여주기
    // EntityGraph는 instructor를 조회할 때 N+1문제 방지 위함
    @EntityGraph(attributePaths = {"instructor"})
    List<LiveLecture> findByStatusInOrderByStartAtAsc(List<LiveLectureStatus> statuses);

    @EntityGraph(attributePaths = {"instructor"})
    List<LiveLecture> findByStatusInAndEndAtAfterOrderByStartAtAsc(
            List<LiveLectureStatus> statuses,
            LocalDateTime now
    );

    // 비관적 락 추가
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from LiveLecture l where l.id = :liveId")
    Optional<LiveLecture> findByIdForUpdate(@Param("liveId") Long liveId);


}
