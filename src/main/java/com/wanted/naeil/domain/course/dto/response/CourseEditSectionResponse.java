package com.wanted.naeil.domain.course.dto.response;

import com.wanted.naeil.domain.course.entity.Section;
import com.wanted.naeil.domain.course.entity.enums.SectionStatus;
import lombok.Builder;
import lombok.Getter;

// 코스 수정 시, 기존 섹션 정보 리스트
@Getter
@Builder
public class CourseEditSectionResponse {

    private Long sectionId;
    private String title;
    private String videoUrl;
    private String playTime;
    private String attachmentUrl;
    private Boolean isFree;
    private Boolean active;

    public static CourseEditSectionResponse from(Section section) {
        return CourseEditSectionResponse.builder()
                .sectionId(section.getId())
                .title(section.getTitle())
                .videoUrl(section.getVideoUrl())
                .playTime(section.getPlayTime() != null ? section.getPlayTime().toString() : "00:00:00")
                .attachmentUrl(section.getAttachmentUrl())
                .isFree(section.getIsFree())
                .active(section.getStatus() == SectionStatus.ACTIVE)
                .build();

    }
}
