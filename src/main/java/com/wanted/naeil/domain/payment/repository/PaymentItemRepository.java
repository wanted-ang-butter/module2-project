package com.wanted.naeil.domain.payment.repository;

import com.wanted.naeil.domain.payment.entity.PaymentItem;
import com.wanted.naeil.domain.payment.entity.enums.PaymentItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentItemRepository extends JpaRepository<PaymentItem, Long> {

    Page<PaymentItem> findByPayment_User_Id(Long userId, Pageable pageable);

    Page<PaymentItem> findByPayment_User_IdAndItemType(
            Long userId,
            PaymentItemType itemType,
            Pageable pageable
    );

    List<PaymentItem> findByCourse_Instructor_IdAndItemType(Long instructorId, PaymentItemType itemType);
}