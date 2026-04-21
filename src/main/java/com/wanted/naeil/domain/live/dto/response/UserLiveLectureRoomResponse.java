package com.wanted.naeil.domain.live.dto.response;

import com.wanted.naeil.domain.live.entity.LiveLecture;
import com.wanted.naeil.domain.live.entity.enums.LiveLectureStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserLiveLectureRoomResponse {

    private Long liveId;
    private LiveLectureStatus status;
    private String title;
    private String description;
    private String instructorName;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String streamingUrl;
    private int currentCount;
    private int maxCapacity;
    private boolean liveNow;

    public static UserLiveLectureRoomResponse of(LiveLecture liveLecture, boolean liveNow) {
        return UserLiveLectureRoomResponse.builder()
                .liveId(liveLecture.getId())
                .status(liveLecture.getStatus())
                .title(liveLecture.getTitle())
                .description(liveLecture.getDescription())
                .instructorName(liveLecture.getInstructor().getName())
                .startAt(liveLecture.getStartAt())
                .endAt(liveLecture.getEndAt())
                .streamingUrl(liveLecture.getStreamingUrl())
                .currentCount(liveLecture.getCurrentCount())
                .maxCapacity(liveLecture.getMaxCapacity())
                .liveNow(liveNow)
                .build();
    }
}
