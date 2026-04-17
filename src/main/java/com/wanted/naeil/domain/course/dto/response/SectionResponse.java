package com.wanted.naeil.domain.course.dto.response;

import com.wanted.naeil.domain.course.entity.Section;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class SectionResponse {
    private final Long sectionId;
    private final String title;
    private final String videoUrl;
    private final String playTime;
    private final boolean isFree;

    public static SectionResponse from(Section section) {
        return SectionResponse.builder()
                .sectionId(section.getId())
                .title(section.getTitle())
                .videoUrl(section.getVideoUrl())
                .playTime(section.getPlayTime() != null ? section.getPlayTime().toString() : "00:00")
                .isFree(section.getIsFree())
                .build();
    }
}
