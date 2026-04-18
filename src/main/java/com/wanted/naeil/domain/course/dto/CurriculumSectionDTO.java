package com.wanted.naeil.domain.course.dto;

import com.wanted.naeil.domain.learning.entity.enums.ProgressStatus;

import java.time.LocalTime;

// 사이드 강의 목록 정보
public record CurriculumSectionDTO(
        Long sectionId,
        String title,
        // TODO : 이거 String 으로 바꿔도 알아서 바인딩 해주지 않나?
        LocalTime playTime,
        ProgressStatus progressStatus
) {
}
