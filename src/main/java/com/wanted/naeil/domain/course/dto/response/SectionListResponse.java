package com.wanted.naeil.domain.course.dto.response;

import com.wanted.naeil.domain.course.entity.Section;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SectionListResponse {
    private final Long sectionId;
    private final String title;
    private final String videoUrl;
    private final String playTime;
    private final boolean isFree;

    public static SectionListResponse from(Section section) {
        return SectionListResponse.builder()
                .sectionId(section.getId())
                .title(section.getTitle())
                .videoUrl(section.getVideoUrl())
                .playTime(section.getPlayTime() != null ? section.getPlayTime().toString() : "00:00")
                .isFree(section.getIsFree())
                .build();
    }
}
