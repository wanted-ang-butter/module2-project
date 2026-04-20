package com.wanted.naeil.domain.live.dto.response;

import com.wanted.naeil.domain.live.entity.LiveLecture;
import com.wanted.naeil.domain.live.entity.enums.LiveLectureStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InstructorLiveLectureResponse {

    private Long liveId;
    private LiveLectureStatus status;
    private String statusDescription;
    private String title;
    private LocalDateTime startAt;
    private int currentCount;
    private int maxCapacity;

    public static InstructorLiveLectureResponse of(LiveLecture liveLecture) {
        return InstructorLiveLectureResponse.builder()
                .liveId(liveLecture.getId())
                .status(liveLecture.getStatus())
                .statusDescription(liveLecture.getStatus().getDescription())
                .title(liveLecture.getTitle())
                .startAt(liveLecture.getStartAt())
                .currentCount(liveLecture.getCurrentCount())
                .maxCapacity(liveLecture.getMaxCapacity())
                .build();
    }

}
