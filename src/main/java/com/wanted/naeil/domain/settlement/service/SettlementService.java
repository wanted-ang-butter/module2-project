package com.wanted.naeil.domain.settlement.service;

import com.wanted.naeil.domain.settlement.entity.Settlement;
import com.wanted.naeil.domain.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

    private final SettlementRepository settlementRepository;

    // 강사 정산 목록 조회
    public List<Settlement> getMySettlements(Long instructorId) {
        return settlementRepository.findAllByInstructor_IdOrderBySettlementMonthDesc(instructorId);
    }

    // 강사 본인 정산 1건 조회
    public Settlement getMySettlement(Long instructorId, Long settlementId) {
        return settlementRepository.findByIdAndInstructor_Id(settlementId, instructorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 정산 내역을 찾을 수 없습니다."));
    }

    // 정산 신청
    @Transactional
    public void requestSettlement(Long instructorId, Long settlementId) {
        Settlement settlement = settlementRepository.findByIdAndInstructor_Id(settlementId, instructorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 정산 내역을 찾을 수 없습니다."));

        settlement.request();
    }

    // 정산 취소
    @Transactional
    public void cancelSettlement(Long instructorId, Long settlementId) {
        Settlement settlement = settlementRepository.findByIdAndInstructor_Id(settlementId, instructorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 정산 내역을 찾을 수 없습니다."));

        settlement.cancel();
    }
}