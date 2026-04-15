package com.wanted.naeil.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CartSummaryResponse {

    private int selectedCount;
    private int totalRequiredCredit;
    private int currentCredit;
    private int remainingCredit;
    private boolean canPurchase;
}
