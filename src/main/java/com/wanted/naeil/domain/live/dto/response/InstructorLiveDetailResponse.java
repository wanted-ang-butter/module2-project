package com.wanted.naeil.domain.live.dto.response;

import com.wanted.naeil.domain.live.entity.LiveLecture;
import com.wanted.naeil.domain.live.entity.enums.LiveLectureStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InstructorLiveDetailResponse {

    private Long liveId;
    private LiveLectureStatus status;
    private String statusDescription;
    private String title;
    private String description;
    private LocalDateTime reservationStartAt;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String streamingUrl;
    private int currentCount;
    private int maxCapacity;
    private LocalDateTime createdAt;

    public static InstructorLiveDetailResponse of(LiveLecture lecture) {
        return InstructorLiveDetailResponse.builder()
                .liveId(lecture.getId())
                .status(lecture.getStatus())
                .statusDescription(lecture.getStatus().getDescription())
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .reservationStartAt(lecture.getReservationStartAt())
                .startAt(lecture.getStartAt())
                .endAt(lecture.getEndAt())
                .streamingUrl(lecture.getStreamingUrl())
                .currentCount(lecture.getCurrentCount())
                .maxCapacity(lecture.getMaxCapacity())
                .createdAt(lecture.getCreatedAt())
                .build();
    }
}
