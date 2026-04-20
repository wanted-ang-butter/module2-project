package com.wanted.naeil.domain.payment.service;

import com.wanted.naeil.domain.payment.entity.Credit;
import com.wanted.naeil.domain.payment.repository.CreditRepository;
import com.wanted.naeil.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;

    // 크레딧 충전
    @Transactional
    public void chargeCredit(Long userId, int amount) {
        Credit credit = creditRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("크레딧 정보가 없습니다."));

        credit.charge(amount);
    }

    // 크레딧 사용
    @Transactional
    public void deductCredit(Long userId, int amount) {
        Credit credit = creditRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("크레딧 정보가 존재하지 않습니다."));

        credit.deduct(amount);
    }

    // 잔액 조회
    @Transactional(readOnly = true)
    public int getBalance(Long userId) {
        return creditRepository.findById(userId)
                .map(Credit::getBalance)
                .orElse(0);
    }

}