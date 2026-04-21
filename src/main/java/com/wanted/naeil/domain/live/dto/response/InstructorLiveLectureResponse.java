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
    private String description;
    private String instructorName;
    private LocalDateTime startAt;
    private LocalDateTime createdAt;
    private int currentCount;
    private int maxCapacity;

    public static InstructorLiveLectureResponse of(LiveLecture liveLecture) {
        return InstructorLiveLectureResponse.builder()
                .liveId(liveLecture.getId())
                .status(liveLecture.getStatus())
                .statusDescription(liveLecture.getStatus().getDescription())
                .title(liveLecture.getTitle())
                .description(liveLecture.getDescription())
                .instructorName(liveLecture.getInstructor().getNickname())
                .startAt(liveLecture.getStartAt())
                .createdAt(liveLecture.getCreatedAt())
                .currentCount(liveLecture.getCurrentCount())
                .maxCapacity(liveLecture.getMaxCapacity())
                .build();
    }

}
