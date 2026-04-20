package com.wanted.naeil.domain.settlement.repository;

import com.wanted.naeil.domain.settlement.entity.Settlement;
import com.wanted.naeil.domain.settlement.entity.enums.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    // 강사 정산 목록 조회 (최신순)
    List<Settlement> findAllByInstructor_IdOrderBySettlementMonthDesc(Long instructorId);

    // 강사 본인 정산 1건 조회 (보안 중요)
    Optional<Settlement> findByIdAndInstructor_Id(Long settlementId, Long instructorId);

    // 월별 정산 중복 체크
    boolean existsByInstructor_IdAndSettlementMonth(Long instructorId, String settlementMonth);

    // 상태별 조회 (선택)
    List<Settlement> findAllByInstructor_IdAndStatus(Long instructorId, SettlementStatus status);
}