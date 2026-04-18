package com.wanted.naeil.domain.course.service;

import com.wanted.naeil.domain.course.dto.CurriculumSectionDTO;
import com.wanted.naeil.domain.course.dto.SectionStudyMainDTO;
import com.wanted.naeil.domain.course.dto.request.UploadSectionRequest;
import com.wanted.naeil.domain.course.dto.response.SectionResponse;
import com.wanted.naeil.domain.course.dto.response.SectionStudyResponse;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.entity.Section;
import com.wanted.naeil.domain.course.entity.enums.SectionStatus;
import com.wanted.naeil.domain.course.repository.SectionRepository;
import com.wanted.naeil.domain.learning.entity.enums.EnrollmentStatus;
import com.wanted.naeil.domain.learning.entity.enums.ProgressStatus;
import com.wanted.naeil.domain.learning.repository.EnrollmentRepository;
import com.wanted.naeil.global.util.file.LocalFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionService {

    private final SectionRepository sectionRepository;
    private final LocalFileService localFileService;
    private final EnrollmentRepository enrollmentRepository;
    // 시간 포멧팅
    private static final DateTimeFormatter PLAY_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // 섹션 등록
    public void createSection(Course course, List<UploadSectionRequest> sectionRequests) {

        // 섹션 존재 검증 로직
        if (sectionRequests == null || sectionRequests.isEmpty()) {
            return;
        }

        log.info("[section] 섹션 등록 시작, 소속 코스 ID: {} ", course.getId());
        List<Section> sectionList = new ArrayList<>();

        for (int i = 0; i < sectionRequests.size(); i++) {
            UploadSectionRequest request = sectionRequests.get(i);

            int savedSequence = i + 1;

            // 비디오 파일 저장 처리 (필수)
            MultipartFile videoFile = request.getVideoFile();

            // 파일 존재 검증
            if (videoFile == null || videoFile.isEmpty()) {
                throw new IllegalArgumentException(savedSequence + "주차 강의의 동영상 파일이 누락되었습니다.");
            }

            String uploadedVideoUrl = localFileService.uploadSingleFile(videoFile, "videos");

            MultipartFile attachmentFile = request.getAttachmentFile();

            // 첨부 파일 업로드 기능 (선택)
            String uploadedAttachmentUrl = null;
            if (attachmentFile != null && !attachmentFile.isEmpty()) {
                uploadedAttachmentUrl = localFileService.uploadSingleFile(attachmentFile, "attachments");
            }

           SectionStatus uploadedStatus = (request.getIsActive() != null ? SectionStatus.ACTIVE : SectionStatus.INACTIVE);

            Section section = Section.builder()
                    .course(course)
                    .title(request.getTitle())
                    .videoUrl(uploadedVideoUrl)
                    .playTime(request.getPlayTime())
                    .attachmentUrl(uploadedAttachmentUrl)
                    .sequence(savedSequence)
                    .isFree(request.getIsFree())
                    .status(uploadedStatus)
                    .build();

            sectionList.add(section);
        }

        sectionRepository.saveAll(sectionList);
        log.info("[섹션 생성] 코스 ID: {}에 총 {}개의 섹션이 정상적으로 저장되었습니다.", course.getId(), sectionList.size());
    }

    // 섹션 전체 조회
    @Transactional(readOnly = true)
    public List<SectionResponse> getSectionsByCourseId(Long courseId) {
        log.info("[Section] 코스 ID: {}의 섹션 목록 조회", courseId);

        return sectionRepository.findByCourseId(courseId).stream()
                .map(SectionResponse::from)
                .collect(Collectors.toList());
    }

    // 세션 상세 조회 (강의 수강 기능)
    @Transactional(readOnly = true)
    public SectionStudyResponse getSectionStudyPage(Long userId, Long courseId, Long sectionId) {

        // 수강 중인 강의인지 검증
        EnrollmentStatus enrollmentStatus = enrollmentRepository
                .findStatusByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new AccessDeniedException("수강 중인 강의가 아닙니다."));

        // 섹션의 존재 여부 검증
        SectionStudyMainDTO mainDTO = sectionRepository
                .findSectionStudyMain(userId, courseId, sectionId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 섹션입니다."));

        // 사이드 강의 목록 조회
        List<CurriculumSectionDTO> curriculumDTO =
                sectionRepository.findCurriculumSections(userId, courseId);

        // 사이드 강의 목록 데이터 담기
        List<SectionStudyResponse.CurriculumSectionInfo> curriculumSections =
                curriculumDTO.stream()
                        .map(dto -> SectionStudyResponse.CurriculumSectionInfo.builder()
                                .sectionId(dto.sectionId())
                                .title(dto.title())
                                .playTime(formatPlayTime(dto.playTime()))
                                .progressStatus(dto.progressStatus())
                                .current(dto.sectionId().equals(sectionId))
                                .build())
                        .toList();

        int total = curriculumSections.size();

        int completed = (int) curriculumSections.stream()
                .filter(section -> section.getProgressStatus() == ProgressStatus.COMPLETED)
                .count();

        // 평균 수강률
        int progressRate = total == 0 ? 0 : completed*100 / total;

        return SectionStudyResponse.builder()
                .course(SectionStudyResponse.CourseInfo.builder()
                        .courseId(mainDTO.courseId())
                        .enrollmentStatus(enrollmentStatus)
                        .category(mainDTO.category())
                        .courseTitle(mainDTO.courseTitle())
                        .instructorName(mainDTO.instructorName())
                        .build())
                .video(SectionStudyResponse.VideoInfo.builder()
                        .sectionId(mainDTO.sectionId())
                        .videoUrl(mainDTO.videoUrl())
                        .sectionTitle(mainDTO.sectionTitle())
                        .progressStatus(mainDTO.progressStatus()) // null 처리 해줘야되나..?
                        .attachmentUrl(mainDTO.attachmentUrl())
                        .build())
                .curriculum(SectionStudyResponse.CurriculumInfo.builder()
                        .progressRate(progressRate)
                        .totalSectionCount(total)
                        .completedSectionCount(completed)
                        .sections(curriculumSections)
                        .build())
                .build();
    }

    // ============= 내부 편의 메서드 ================

    private String formatPlayTime(LocalTime playTime) {
        return playTime != null
                ? playTime.format(PLAY_TIME_FORMATTER)
                : "00:00";
    }
}
