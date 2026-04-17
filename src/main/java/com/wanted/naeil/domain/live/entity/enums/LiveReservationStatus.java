package com.wanted.naeil.domain.live.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LiveReservationStatus {
    RESERVED("예약 완료"),
    CANCELED("취소됨");

    private final String description;

}
