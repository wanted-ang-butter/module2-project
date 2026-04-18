package com.wanted.naeil.domain.course.service;

import com.wanted.naeil.domain.course.dto.request.UploadSectionRequest;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.entity.Section;
import com.wanted.naeil.domain.course.entity.enums.SectionStatus;
import com.wanted.naeil.domain.course.repository.SectionRepository;
import com.wanted.naeil.global.util.file.LocalFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionService {

    private final SectionRepository sectionRepository;
    private final LocalFileService localFileService;

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
}
