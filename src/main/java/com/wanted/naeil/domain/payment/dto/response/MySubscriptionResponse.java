package com.wanted.naeil.domain.payment.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MySubscriptionResponse {

    private boolean subscribed;
    private String planType;
    private String planName;
    private String status;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private Integer remainingFreeCount;
    private Integer usedFreeCount;
    private Integer totalFreeCount;

    private Boolean autoRenew;
}