package com.wanted.naeil.domain.settlement.service;

import com.wanted.naeil.domain.admin.entity.AdminApproval;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalRequestType;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalStatus;
import com.wanted.naeil.domain.admin.repository.AdminApprovalRepository;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.payment.entity.PaymentItem;
import com.wanted.naeil.domain.payment.entity.enums.PaymentItemType;
import com.wanted.naeil.domain.payment.entity.enums.PaymentStatus;
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

    private static final double PLATFORM_FEE_RATE = 0.1;

    private final SettlementRepository settlementRepository;
    private final SettlementDetailRepository settlementDetailRepository;
    private final PaymentItemRepository paymentItemRepository;
    private final UserRepository userRepository;
    private final AdminApprovalRepository adminApprovalRepository;

    public List<Settlement> getMySettlements(Long instructorId) {
        return settlementRepository.findAllByInstructor_IdOrderBySettlementMonthDesc(instructorId);
    }

    @Transactional
    public void reflectCoursePayments(List<PaymentItem> paymentItems, LocalDateTime paidAt) {
        if (paymentItems == null || paymentItems.isEmpty()) {
            return;
        }

        LocalDateTime paidDateTime = paidAt != null ? paidAt : LocalDateTime.now();
        String settlementMonth = YearMonth.from(paidDateTime).toString();

        List<PaymentItem> coursePayments = paymentItems.stream()
                .filter(item -> item.getItemType() == PaymentItemType.COURSE)
                .filter(item -> item.getCourse() != null)
                .filter(item -> item.getFinalPrice() > 0)
                .toList();

        for (PaymentItem paymentItem : coursePayments) {
            Course course = paymentItem.getCourse();
            User instructor = course.getInstructor();
            int saleAmount = paymentItem.getFinalPrice();
            int platformFee = calculatePlatformFee(saleAmount);
            int settlementAmount = saleAmount - platformFee;

            Settlement settlement = settlementRepository
                    .findByInstructor_IdAndSettlementMonth(instructor.getId(), settlementMonth)
                    .orElseGet(() -> settlementRepository.save(
                            Settlement.builder()
                                    .instructor(instructor)
                                    .admin(null)
                                    .settlementMonth(settlementMonth)
                                    .totalSalesAmount(0)
                                    .platformFee(0)
                                    .finalAmount(0)
                                    .totalAmount(0)
                                    .requestedAmount(0)
                                    .status(SettlementStatus.READY)
                                    .createdAt(paidDateTime)
                                    .completedAt(null)
                                    .build()
                    ));

            settlement.accumulateSale(saleAmount, platformFee, settlementAmount);

            SettlementDetail detail = settlement.findDetailByCourseId(course.getId())
                    .orElseGet(() -> {
                        SettlementDetail newDetail = SettlementDetail.builder()
                                .course(course)
                                .saleCount(0)
                                .totalSalesAmount(0)
                                .finalAmount(0)
                                .build();
                        settlement.addDetail(newDetail);
                        return newDetail;
                    });

            detail.accumulateSale(1, saleAmount, settlementAmount);
        }
    }

    public Settlement getMySettlement(Long instructorId, Long settlementId) {
        return settlementRepository.findByIdAndInstructor_Id(settlementId, instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found."));
    }

    @Transactional
    public Long createAndRequestSettlement(Long instructorId, YearMonth settlementMonth) {
        String settlementMonthValue = settlementMonth.toString();

        Settlement existingSettlement = settlementRepository
                .findByInstructor_IdAndSettlementMonth(instructorId, settlementMonthValue)
                .orElse(null);

        if (existingSettlement != null) {
            if (existingSettlement.getStatus() == SettlementStatus.APPROVED) {
                throw new IllegalStateException("This settlement has already been approved.");
            }

            if (existingSettlement.getStatus() != SettlementStatus.PENDING) {
                existingSettlement.request();
            }

            createPendingSettlementApprovalIfMissing(existingSettlement);
            return existingSettlement.getId();
        }

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found."));

        List<PaymentItem> paymentItems =
                paymentItemRepository.findByCourse_Instructor_IdAndItemType(instructorId, PaymentItemType.COURSE);

        List<PaymentItem> settleTargetItems = paymentItems.stream()
                .filter(item -> item.getPayment() != null)
                .filter(item -> item.getPayment().getStatus() == PaymentStatus.SUCCESS)
                .filter(item -> item.getPayment().getPaidAt() != null)
                .filter(item -> YearMonth.from(item.getPayment().getPaidAt()).equals(settlementMonth))
                .filter(item -> item.getFinalPrice() > 0)
                .toList();

        if (settleTargetItems.isEmpty()) {
            throw new IllegalStateException("No settlement items are available for the selected month.");
        }

        int totalSalesAmount = settleTargetItems.stream()
                .mapToInt(PaymentItem::getFinalPrice)
                .sum();

        int platformFee = calculatePlatformFee(totalSalesAmount);
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

        Map<Long, List<PaymentItem>> groupedByCourse = settleTargetItems.stream()
                .collect(Collectors.groupingBy(item -> item.getCourse().getId()));

        for (List<PaymentItem> items : groupedByCourse.values()) {
            PaymentItem firstItem = items.get(0);
            int saleCount = items.size();
            int courseTotalSalesAmount = items.stream()
                    .mapToInt(PaymentItem::getFinalPrice)
                    .sum();
            int coursePlatformFee = calculatePlatformFee(courseTotalSalesAmount);
            int courseFinalAmount = courseTotalSalesAmount - coursePlatformFee;

            SettlementDetail detail = SettlementDetail.builder()
                    .course(firstItem.getCourse())
                    .saleCount(saleCount)
                    .totalSalesAmount(courseTotalSalesAmount)
                    .finalAmount(courseFinalAmount)
                    .build();

            settlement.addDetail(detail);
        }

        settlement.request();
        createPendingSettlementApprovalIfMissing(settlement);

        return settlement.getId();
    }

    @Transactional
    public void requestSettlement(Long instructorId, Long settlementId) {
        Settlement settlement = settlementRepository.findByIdAndInstructor_Id(settlementId, instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found."));

        settlement.request();
        createPendingSettlementApprovalIfMissing(settlement);
    }

    @Transactional
    public void cancelSettlement(Long instructorId, Long settlementId) {
        Settlement settlement = settlementRepository.findByIdAndInstructor_Id(settlementId, instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found."));

        settlement.cancel();
        adminApprovalRepository.deleteBySettlementIdAndRequestTypeAndStatus(
                settlementId,
                ApprovalRequestType.SETTLEMENT_REGISTER,
                ApprovalStatus.PENDING
        );
    }

    @Transactional
    public void syncPendingSettlementApprovals() {
        settlementRepository.findAllByStatus(SettlementStatus.PENDING)
                .forEach(this::createPendingSettlementApprovalIfMissing);
    }

    private int calculatePlatformFee(int salesAmount) {
        return (int) (salesAmount * PLATFORM_FEE_RATE);
    }

    private void createPendingSettlementApprovalIfMissing(Settlement settlement) {
        boolean alreadyRequested = adminApprovalRepository.existsBySettlementIdAndRequestTypeAndStatus(
                settlement.getId(),
                ApprovalRequestType.SETTLEMENT_REGISTER,
                ApprovalStatus.PENDING
        );

        if (alreadyRequested) {
            return;
        }

        adminApprovalRepository.save(new AdminApproval(settlement));
    }
}
