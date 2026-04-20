package com.wanted.naeil.domain.live.repository;

import com.wanted.naeil.domain.live.entity.LiveReservation;
import com.wanted.naeil.domain.live.entity.enums.LiveReservationStatus;
import com.wanted.naeil.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LiveReservationRepository extends JpaRepository<LiveReservation, Long> {


    // 내 강의 페이지 - RESERVED 상태인 예약 목록 조회 (liveLecture fetch join으로 N+1 방지)
    @Query("""
        SELECT r FROM LiveReservation r
        JOIN FETCH r.liveLecture l
        JOIN FETCH l.instructor
        WHERE r.user = :user
        AND r.status = :status
        ORDER BY l.startAt ASC
    """)
    List<LiveReservation> findByUserAndStatusWithLecture(
            @Param("user") User user,
            @Param("status") LiveReservationStatus status
    );
}
