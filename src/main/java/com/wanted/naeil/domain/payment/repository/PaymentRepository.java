package com.wanted.naeil.domain.payment.repository;

import com.wanted.naeil.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
