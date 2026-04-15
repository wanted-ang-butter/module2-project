package com.wanted.naeil.domain.payment.repository;

import com.wanted.naeil.domain.payment.entity.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditRepository extends JpaRepository<Credit, Long> {
}