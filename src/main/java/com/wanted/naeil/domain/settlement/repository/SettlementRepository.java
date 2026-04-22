package com.wanted.naeil.domain.settlement.repository;

import com.wanted.naeil.domain.settlement.entity.Settlement;
import com.wanted.naeil.domain.settlement.entity.enums.SettlementStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    List<Settlement> findAllByInstructor_IdOrderBySettlementMonthDesc(Long instructorId);

    Optional<Settlement> findByIdAndInstructor_Id(Long settlementId, Long instructorId);

    // 성민 수정: 강사/월 기준 기존 정산 row 조회 시 상세/코스까지 함께 로딩
    @EntityGraph(attributePaths = {"details", "details.course"})
    Optional<Settlement> findByInstructor_IdAndSettlementMonth(Long instructorId, String settlementMonth);

    boolean existsByInstructor_IdAndSettlementMonth(Long instructorId, String settlementMonth);

    List<Settlement> findAllByInstructor_IdAndStatus(Long instructorId, SettlementStatus status);

    List<Settlement> findAllByStatus(SettlementStatus status);
}
