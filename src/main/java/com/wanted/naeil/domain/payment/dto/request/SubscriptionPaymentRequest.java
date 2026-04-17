package com.wanted.naeil.domain.payment.dto.request;

import com.wanted.naeil.domain.payment.entity.enums.PlanType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubscriptionPaymentRequest {

    private PlanType planType;  // MONTHLY or YEARLY
    private Boolean autoRenew;  // true / false
}