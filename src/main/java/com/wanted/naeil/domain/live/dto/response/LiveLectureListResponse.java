package com.wanted.naeil.domain.live.dto.response;

import com.wanted.naeil.domain.live.entity.LiveLecture;
import com.wanted.naeil.domain.live.entity.enums.LiveLectureStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LiveLectureListResponse {

    private Long liveId;
    private String instructorName;
    private LiveLectureStatus status;
    private String statusDescription;
    private String title;
    private LocalDateTime reservationStartAt;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int currentCount;
    private int maxCapacity;
    private int reservationRate; // 예약율
    private boolean isClosingSoon; // 마감입박
    private boolean isLive; // live 중인지
    private boolean isFull; // 꽉 찾는지 TODO : 추후에, 만약 꽉차지 않으면 바로 상세 조회 가능하게
    private boolean reservable; // 예약 가능 여부

    public static LiveLectureListResponse of(LiveLecture liveLecture) {

        LocalDateTime now = LocalDateTime.now();

        int maxCapacity = liveLecture.getMaxCapacity();
        int currentCount = liveLecture.getCurrentCount();
        int reservationRate = calculateReservationRate(currentCount, maxCapacity);
        boolean isLive = isLiveNow(liveLecture.getStartAt(), liveLecture.getEndAt(), now);
        boolean isClosingSoon = reservationRate >= 80;
        boolean isFull = currentCount >= maxCapacity;
        boolean isEnded = liveLecture.getEndAt() != null && !now.isBefore(liveLecture.getEndAt());
        boolean reservable = liveLecture.getStatus() == LiveLectureStatus.APPROVED
                && !isEnded
                && !isFull;

        return LiveLectureListResponse.builder()
                .liveId(liveLecture.getId())
                .instructorName(liveLecture.getInstructor().getName())
                .status(liveLecture.getStatus())
                .statusDescription(liveLecture.getStatus().getDescription())
                .title(liveLecture.getTitle())
                .reservationStartAt(liveLecture.getReservationStartAt())
                .startAt(liveLecture.getStartAt())
                .endAt(liveLecture.getEndAt())
                .currentCount(currentCount)
                .maxCapacity(maxCapacity)
                .reservationRate(reservationRate)
                .isClosingSoon(isClosingSoon)
                .isLive(isLive)
                .isFull(isFull)
                .reservable(reservable)
                .build();
    }

    // 라이브 여부
    private static boolean isLiveNow(LocalDateTime startAt, LocalDateTime endAt, LocalDateTime now) {
        if (startAt == null || endAt == null) {
            return false;
        }

        return !now.isBefore(startAt) && now.isBefore(endAt);
    }

    // 예약율 계산
    private static int calculateReservationRate(int currentCount, int maxCapacity) {
        if (maxCapacity <= 0) {
            return 0;
        }

        return currentCount * 100 / maxCapacity;
    }
}
