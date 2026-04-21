package com.wanted.naeil.domain.course.service;

import com.wanted.naeil.domain.course.dto.CurriculumSectionDTO;
import com.wanted.naeil.domain.course.dto.SectionStudyMainDTO;
import com.wanted.naeil.domain.course.dto.request.SectionUpdateRequest;
import com.wanted.naeil.domain.course.dto.request.UploadSectionRequest;
import com.wanted.naeil.domain.course.dto.response.CourseEditSectionResponse;
import com.wanted.naeil.domain.course.dto.response.SectionListResponse;
import com.wanted.naeil.domain.course.dto.response.SectionStudyResponse;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.entity.Section;
import com.wanted.naeil.domain.course.entity.enums.SectionStatus;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import com.wanted.naeil.domain.course.repository.SectionRepository;
import com.wanted.naeil.domain.learning.entity.Enrollment;
import com.wanted.naeil.domain.learning.entity.LearningProgress;
import com.wanted.naeil.domain.learning.entity.enums.EnrollmentStatus;
import com.wanted.naeil.domain.learning.entity.enums.ProgressStatus;
import com.wanted.naeil.domain.learning.repository.EnrollmentRepository;
import com.wanted.naeil.domain.learning.repository.LearningProgressRepository;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import com.wanted.naeil.global.util.file.FileTransactionService;
import com.wanted.naeil.global.util.file.LocalFileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final CourseRepository courseRepository;
    private final LocalFileService localFileService;
    private final FileTransactionService fileTransactionService;
    private final EnrollmentRepository enrollmentRepository;
    private final LearningProgressRepository learningProgressRepository;
    private final UserRepository userRepository;
    // 시간 포멧팅
    private static final DateTimeFormatter PLAY_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // 섹션 전체 등록 (List)
    public void registerSections(Course course, List<UploadSectionRequest> sectionRequests) {

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

    // 섹션 단건 추가
    @Transactional
    public void createSection(Long instructorId, Long courseId, @Valid UploadSectionRequest request) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 강의입니다."));

        validateCourseOwner(course, instructorId);

        MultipartFile videoFile = request.getVideoFile();

        if (videoFile == null || videoFile.isEmpty()) {
            throw new IllegalArgumentException("영상 파일은 필수입니다.");
        }

        String videoUrl = localFileService.uploadSingleFile(videoFile, "videos");

        String attachmentUrl = null;

        if (request.getAttachmentFile() != null &&  !request.getAttachmentFile().isEmpty()) {
            attachmentUrl = localFileService.uploadSingleFile(request.getAttachmentFile(), "attachments");
        }

        int nextSequence = sectionRepository.countByCourseId(courseId) + 1;

        SectionStatus status = Boolean.TRUE.equals(request.getIsActive())
                ? SectionStatus.ACTIVE
                : SectionStatus.INACTIVE;

        Section section = Section.builder()
                .course(course)
                .title(request.getTitle())
                .videoUrl(videoUrl)
                .playTime(request.getPlayTime())
                .attachmentUrl(attachmentUrl)
                .sequence(nextSequence)
                .isFree(request.getIsFree())
                .status(status)
                .build();

        sectionRepository.save(section);

        log.info("[SectionCreate] 섹션 추가 완료 - instructorId: {}, courseId: {}, sectionId: {}",
                instructorId, courseId, section.getId());
    }

    // 섹션 전체 조회 - 유저
    @Transactional(readOnly = true)
    public List<SectionListResponse> getSectionsByCourseId(Long courseId) {
        log.info("[Section] 코스 ID: {}의 섹션 목록 조회", courseId);

        return sectionRepository.findByCourseId(courseId).stream()
                .map(SectionListResponse::from)
                .collect(Collectors.toList());
    }

    // 세션 상세 조회 - 유저 (강의 수강 기능)
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

    // 섹션 수정 페이지 조회 - 강사\
    @Transactional(readOnly = true)
    public List<CourseEditSectionResponse> getSectionEdit(Long courseId) {
        log.info("[sectionEdit] 섹션 전체 조회 시작");

        return sectionRepository.findByCourseId(courseId).stream()
                .map(CourseEditSectionResponse::from)
                .toList();
    }

    // 섹션 정보 수정 - 강사
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INSTRUCTOR')")
    @Transactional
    public void updateSection(Long instructorId, Long courseId, Long sectionId, @Valid SectionUpdateRequest request) {

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 섹션입니다."));

        // 해당 코스에 포함된 섹션인지 검증
        validateSectionBelongsToCourse(section, courseId);

        // 본인 섹션 인지 검증
        validateSectionOwner(section, instructorId);

        String oldVideoUrl = section.getVideoUrl();
        String oldAttachmentUrl = section.getAttachmentUrl();

        String newVideoUrl = null;
        String newAttachmentUrl = null;

        String videoUrl = oldVideoUrl;
        String attachmentUrl = oldAttachmentUrl;

        if (request.getVideoFile() != null && !request.getVideoFile().isEmpty()) {
            newVideoUrl = localFileService.uploadSingleFile(request.getVideoFile(), "videos");
            videoUrl = newVideoUrl;
        }

        if (request.getAttachmentFile() != null && !request.getAttachmentFile().isEmpty()) {
            newAttachmentUrl = localFileService.uploadSingleFile(request.getAttachmentFile(), "attachments");
            attachmentUrl = newAttachmentUrl;
        }

        // Boolean rapper 클래스여서, 그냥 == true하면 null 처리가 안됨.
        // 이거는 null -> inactive 로 들어감
        SectionStatus status = Boolean.TRUE.equals(request.getIsActive())
                ? SectionStatus.ACTIVE
                : SectionStatus.INACTIVE;

        section.updateSectionInfo(
                request.getTitle(),
                request.getPlayTime(),
                request.getIsFree(),
                status
        );

        section.updateVideoUrl(videoUrl);
        section.updateAttachmentUrl(attachmentUrl);

        // CourseService 처럼 파일 처리 과정은 별도 트랜잭션 관리를 위한 로직
        if (newVideoUrl != null) {
            fileTransactionService.registerReplace(oldVideoUrl, newVideoUrl);
        }

        if (newAttachmentUrl != null) {
            fileTransactionService.registerReplace(oldAttachmentUrl, newAttachmentUrl);
        }


        log.info("[SectionUpdate] 섹션 수정 완료 - instructorId: {}, courseId: {}, sectionId: {}",
                instructorId, courseId, sectionId);
    }

    // 섹션 삭제 - 강사
    @Transactional
    public void deleteSection(Long instructorId, Long courseId, Long sectionId) {

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 섹션입니다."));

        validateSectionBelongsToCourse(section, courseId);
        validateSectionOwner(section, instructorId);

        // 논리적 삭제
        sectionRepository.delete(section);

        log.info("[SectionDelete] 섹션 삭제 완료 - instructorId: {}, courseId: {}, sectionId: {}",
                instructorId, courseId, sectionId);
    }

    // 학습 중단
    @Transactional
    public void stopLearning(Long userId, Long courseId, Long sectionId) {
        validateEnrolled(userId, courseId);

        LearningProgress progress = getOrCreateProgress(userId, sectionId);
        progress.stop();

        updateCourseProgress(userId, courseId);
    }

    // 학습 완료
    @Transactional
    public Long completeLearning(Long userId, Long courseId, Long sectionId) {
        validateEnrolled(userId, courseId);

        LearningProgress progress = getOrCreateProgress(userId, sectionId);
        progress.complete();

        updateCourseProgress(userId, courseId);

        return sectionRepository.findNextActiveSectionIds(courseId, sectionId)
                .stream()
                .findFirst()
                .orElse(null);
    }


    // ============= 내부 편의 메서드 ================

    private void validateEnrolled(Long userId, Long courseId) {
        enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new AccessDeniedException("수강 중인 강의가 아닙니다."));
    }

    private LearningProgress getOrCreateProgress(Long userId, Long sectionId) {
        return learningProgressRepository.findByUserIdAndSectionId(userId, sectionId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

                    Section section = sectionRepository.findById(sectionId)
                            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 섹션입니다."));

                    return learningProgressRepository.save(
                            LearningProgress.builder()
                                    .user(user)
                                    .section(section)
                                    .build()
                    );
                });
    }

    private void updateCourseProgress(Long userId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new AccessDeniedException("수강 중인 강의가 아닙니다."));

        int totalCount = sectionRepository.countByCourseIdAndStatus(courseId, SectionStatus.ACTIVE);

        long completedCount = learningProgressRepository.findByUserIdAndSectionCourseId(userId, courseId)
                .stream()
                .filter(progress -> progress.getStatus() == ProgressStatus.COMPLETED)
                .count();

        double rate = totalCount == 0 ? 0 : completedCount * 100.0 / totalCount;

        enrollment.updateProgress(rate);

        if (rate >= 100.0) {
            enrollment.updateStatus(EnrollmentStatus.COMPLETED);
        } else if (rate > 0) {
            enrollment.updateStatus(EnrollmentStatus.IN_PROGRESS);
        }
    }

    private String formatPlayTime(LocalTime playTime) {
        return playTime != null
                ? playTime.format(PLAY_TIME_FORMATTER)
                : "00:00";
    }

    private void validateSectionOwner(Section section, Long instructorId) {
        if (!section.getCourse().getInstructor().getId().equals(instructorId)) {
            throw new AccessDeniedException("본인이 생성한 강의의 섹션만 수정할 수 있습니다.");
        }
    }

    private void validateSectionBelongsToCourse(Section section, Long courseId) {
        if (!section.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("해당 강의에 포함된 섹션이 아닙니다.");
        }
    }

    private void validateCourseOwner(Course course, Long instructorId) {
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new AccessDeniedException("본인이 생성한 강의에만 섹션을 추가할 수 있습니다.");
        }
    }
}
