package com.wanted.naeil.domain.course.dto.response;

import com.wanted.naeil.domain.learning.entity.enums.EnrollmentStatus;
import com.wanted.naeil.domain.learning.entity.enums.ProgressStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SectionStudyResponse {

    private CourseInfo course;
    private VideoInfo video;
    private CurriculumInfo curriculum;

    // 상단 코스 정보
    @Getter
    @Builder
    public static class CourseInfo {
        private Long courseId;
        private EnrollmentStatus enrollmentStatus;
        private String category;
        private String courseTitle;
        private String instructorName;
    }

    // 현재 상세 조회 중인 섹션 정보
    @Getter
    @Builder
    public static class VideoInfo {
        private Long sectionId;
        private String videoUrl;
        private String sectionTitle;
        private ProgressStatus  progressStatus;
        private String attachmentUrl;
    }


    // 우측 강의 목차 전체 정보
    @Getter
    @Builder
    public static class CurriculumInfo {
        private int progressRate;
        private int totalSectionCount;
        private int completedSectionCount;
        private List<CurriculumSectionInfo> sections;
    }

    // 목차 속 개별 섹션 정보들
    @Getter
    @Builder
    public static class CurriculumSectionInfo {
        private Long sectionId;
        private String title;
        private String playTime;
        private ProgressStatus progressStatus;
        private Boolean current;
    }
}
