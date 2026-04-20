package com.wanted.naeil.domain.live.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyLiveReservationResponse {

    // 예약 정보
    private Long reservationId;

    // 실시간 강의 정보
    private Long liveId;
    private String title;
    private String instructorName;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer currentCount;
    private Integer maxCapacity;
}
