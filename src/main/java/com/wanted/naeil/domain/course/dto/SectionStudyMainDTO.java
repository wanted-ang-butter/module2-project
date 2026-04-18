package com.wanted.naeil.domain.course.dto;

import com.wanted.naeil.domain.learning.entity.enums.ProgressStatus;

// 현재 수강 중인 강의, 섹션 정보
public record SectionStudyMainDTO(
        Long courseId,
        String category,
        String courseTitle,
        String instructorName,
        Long sectionId,
        String sectionTitle,
        String videoUrl,
        String attachmentUrl,
        ProgressStatus progressStatus
) {
}
