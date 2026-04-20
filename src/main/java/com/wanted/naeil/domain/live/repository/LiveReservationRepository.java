package com.wanted.naeil.domain.live.repository;

import com.wanted.naeil.domain.live.entity.LiveLecture;
import com.wanted.naeil.domain.live.entity.LiveReservation;
import com.wanted.naeil.domain.live.entity.enums.LiveReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LiveReservationRepository extends JpaRepository<LiveReservation, Long> {

    boolean existsByUser_IdAndLiveLecture_IdAndStatus(
            Long userId,
            Long liveId,
            LiveReservationStatus status
    );

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
}
