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
    private boolean isMyReserved; // 내 예약 여부

    public static LiveLectureListResponse of(
            LiveLecture liveLecture,
            int reservationRate,
            boolean isClosingSoon,
            boolean isLive,
            boolean isFull,
            boolean reservable,
            boolean isMyReserved
    ) {
        return LiveLectureListResponse.builder()
                .liveId(liveLecture.getId())
                .instructorName(liveLecture.getInstructor().getName())
                .status(liveLecture.getStatus())
                .statusDescription(liveLecture.getStatus().getDescription())
                .title(liveLecture.getTitle())
                .reservationStartAt(liveLecture.getReservationStartAt())
                .startAt(liveLecture.getStartAt())
                .endAt(liveLecture.getEndAt())
                .currentCount(liveLecture.getCurrentCount())
                .maxCapacity(liveLecture.getMaxCapacity())
                .reservationRate(reservationRate)
                .isClosingSoon(isClosingSoon)
                .isLive(isLive)
                .isFull(isFull)
                .reservable(reservable)
                .isMyReserved(isMyReserved)
                .build();
    }
}
