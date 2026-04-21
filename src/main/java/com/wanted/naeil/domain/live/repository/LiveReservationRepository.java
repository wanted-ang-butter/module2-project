package com.wanted.naeil.domain.live.repository;

import com.wanted.naeil.domain.live.entity.LiveReservation;
import com.wanted.naeil.domain.live.entity.enums.LiveReservationStatus;
import com.wanted.naeil.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface LiveReservationRepository extends JpaRepository<LiveReservation, Long> {

    @Query("""
        select r.liveLecture.id
        from LiveReservation r
        where r.user.id = :userId
          and r.status = :status
        """)
    List<Long> findLiveIdsByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") LiveReservationStatus status
    );

    // 내 강의 페이지 - RESERVED 상태인 예약 목록 조회
    @Query("""
        select r
        from LiveReservation r
        join fetch r.liveLecture l
        join fetch l.instructor
        where r.user = :user
          and r.status = :status
        order by l.startAt asc
        """)
    List<LiveReservation> findByUserAndStatusWithLecture(
            @Param("user") User user,
            @Param("status") LiveReservationStatus status
    );

    // 현재 사용자가 예약 중인지 확인
    Optional<LiveReservation> findByUserIdAndLiveLectureIdAndStatus(
            Long userId,
            Long liveId,
            LiveReservationStatus status
    );

    // 같은 강의 취소 횟수 계산
    long countByUserIdAndLiveLectureIdAndStatus(
            Long userId,
            Long liveId,
            LiveReservationStatus status
    );

    // 사용자가 해당 실시간 강의를 CANCELED 상태로 가진 예약 중,
    // 가장 최근에 취소된 예약 1건을 가져온다.
    Optional<LiveReservation> findTopByUserIdAndLiveLectureIdAndStatusOrderByUpdatedAtDesc(
            Long userId,
            Long liveId,
            LiveReservationStatus status
    );

    // 실시간 강의 신청한 학생들 조회
    @Query("""
    select r
    from LiveReservation r
    join fetch r.user
    where r.liveLecture.id = :liveId
      and r.status = :status
    order by r.createdAt desc
    """)
    List<LiveReservation> findByLiveLectureIdAndStatusWithUserOrderByCreatedAtDesc(
            @Param("liveId") Long liveId,
            @Param("status") LiveReservationStatus status
    );


    // 실시간 강의 예매 검증
    @Query("""
    select r
    from LiveReservation r
    join fetch r.liveLecture l
    join fetch l.instructor
    where r.user.id = :userId
      and l.id = :liveId
      and r.status = :status
    """)
    Optional<LiveReservation> findReservedLiveRoomByUserIdAndLiveId(
            @Param("userId") Long userId,
            @Param("liveId") Long liveId,
            @Param("status") LiveReservationStatus status
    );
}
