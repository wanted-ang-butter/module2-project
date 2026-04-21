package com.wanted.naeil.domain.settlement.service;

import com.wanted.naeil.domain.payment.entity.PaymentItem;
import com.wanted.naeil.domain.payment.entity.enums.PaymentItemType;
import com.wanted.naeil.domain.payment.repository.PaymentItemRepository;
import com.wanted.naeil.domain.settlement.entity.Settlement;
import com.wanted.naeil.domain.settlement.entity.SettlementDetail;
import com.wanted.naeil.domain.settlement.entity.enums.SettlementStatus;
import com.wanted.naeil.domain.settlement.repository.SettlementDetailRepository;
import com.wanted.naeil.domain.settlement.repository.SettlementRepository;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final SettlementDetailRepository settlementDetailRepository;
    private final PaymentItemRepository paymentItemRepository;
    private final UserRepository userRepository;

    // 강사 정산 목록 조회
    public List<Settlement> getMySettlements(Long instructorId) {
        return settlementRepository.findAllByInstructor_IdOrderBySettlementMonthDesc(instructorId);
    }

    // 강사 본인 정산 1건 조회
    public Settlement getMySettlement(Long instructorId, Long settlementId) {
        return settlementRepository.findByIdAndInstructor_Id(settlementId, instructorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 정산 내역을 찾을 수 없습니다."));
    }

    // 정산 생성 후 신청
    @Transactional
    public Long createAndRequestSettlement(Long instructorId, YearMonth settlementMonth) {

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("강사 정보를 찾을 수 없습니다."));

        String settlementMonthValue = settlementMonth.toString(); // 예: 2026-04

        // 같은 월 정산 중복 생성 방지
        boolean exists = settlementRepository.existsByInstructor_IdAndSettlementMonth(instructorId, settlementMonthValue);
        if (exists) {
            throw new IllegalStateException("이미 해당 월의 정산 내역이 존재합니다.");
        }

        // 강사가 맡은 강의의 결제 내역 조회
        List<PaymentItem> paymentItems =
                paymentItemRepository.findByCourse_Instructor_IdAndItemType(instructorId, PaymentItemType.COURSE);

        // TODO:
        // 실제로는 아래 조건을 더 붙여야 함
        // 1) 결제 성공 건만
        // 2) 해당 정산월에 해당하는 건만
        // 3) 이미 정산된 건 제외
        // 현재는 엔티티 추가 없이 가는 방향이라 최소 구조만 반영

        // 0원 결제(구독 무료) 제외
        List<PaymentItem> settleTargetItems = paymentItems.stream()
                .filter(item -> item.getFinalPrice() > 0)
                .toList();

        if (settleTargetItems.isEmpty()) {
            throw new IllegalStateException("정산 가능한 내역이 없습니다.");
        }

        int totalSalesAmount = settleTargetItems.stream()
                .mapToInt(PaymentItem::getFinalPrice)
                .sum();

        // TODO: 수수료 정책 확정 시 수정
        int platformFee = (int) (totalSalesAmount * 0.1);

        int finalAmount = totalSalesAmount - platformFee;

        Settlement settlement = Settlement.builder()
                .instructor(instructor)
                .admin(null)
                .settlementMonth(settlementMonthValue)
                .totalSalesAmount(totalSalesAmount)
                .platformFee(platformFee)
                .finalAmount(finalAmount)
                .totalAmount(finalAmount)
                .requestedAmount(finalAmount)
                .status(SettlementStatus.READY)
                .createdAt(LocalDateTime.now())
                .completedAt(null)
                .build();

        settlementRepository.save(settlement);

        // 코스별 집계
        Map<Long, List<PaymentItem>> groupedByCourse = settleTargetItems.stream()
                .collect(Collectors.groupingBy(item -> item.getCourse().getId()));

        for (List<PaymentItem> items : groupedByCourse.values()) {
            PaymentItem firstItem = items.get(0);

            int saleCount = items.size();

            int courseTotalSalesAmount = items.stream()
                    .mapToInt(PaymentItem::getFinalPrice)
                    .sum();

            int courseFinalAmount = (int) (courseTotalSalesAmount * 0.9);

            SettlementDetail detail = SettlementDetail.builder()
                    .course(firstItem.getCourse())
                    .saleCount(saleCount)
                    .totalSalesAmount(courseTotalSalesAmount)
                    .finalAmount(courseFinalAmount)
                    .build();

            settlement.addDetail(detail);
        }

        // detail cascade 저장 반영
        settlement.request();

        return settlement.getId();
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