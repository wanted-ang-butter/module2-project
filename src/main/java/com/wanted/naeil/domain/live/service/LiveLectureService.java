package com.wanted.naeil.domain.live.service;

import com.wanted.naeil.domain.admin.entity.AdminApproval;
import com.wanted.naeil.domain.admin.repository.AdminApprovalRepository;
import com.wanted.naeil.domain.live.dto.request.CreateLiveLectureRequest;
import com.wanted.naeil.domain.live.dto.response.InstructorLiveLectureResponse;
import com.wanted.naeil.domain.live.entity.LiveLecture;
import com.wanted.naeil.domain.live.repository.LiveLectureRepository;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.entity.enums.Role;
import com.wanted.naeil.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveLectureService {

    private final LiveLectureRepository liveLectureRepository;
    private final UserRepository userRepository;
    private final AdminApprovalRepository adminApprovalRepository;

    // 실시간 강의 등록
    @Transactional
    public String registerLiveLecture(Long instructorId, @Valid CreateLiveLectureRequest request) {

        // 사용자 존재 확인
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("강사 정보를 찾을 수 없습니다. ID: " + instructorId));

        // 관리자 role값 체크
        if (instructor.getRole() != Role.INSTRUCTOR && instructor.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("강사 권한이 있는 사용자만 강의를 등록할 수 있습니다.");
        }

        // 제목 체크
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("강의 제목 필수 입력 값입니다.");
        }

        // 설명 체크
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("강의 설명은 필수 입력 값입니다.");
        }

        // 수강 정원 체크
        if (request.getMaxCapacity() == null || request.getMaxCapacity() < 1) {
            throw new IllegalArgumentException("올바른 수강 정원 값을 입력해주세요.");
        }

        // 방송 url 체크
        if (request.getStreamingUrl() == null || request.getStreamingUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("방송 URL은 필수 입력 값 입니다.");
        }

        // 시간 관련 검증
        validateLiveLectureTime(request);

        LiveLecture liveLecture = LiveLecture.builder()
                .instructor(instructor)
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .maxCapacity(request.getMaxCapacity())
                .reservationStartAt(request.getReservationStartAt())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .streamingUrl(request.getStreamingUrl().trim())
                .build();

        // 저장
        LiveLecture savedLiveLecture = liveLectureRepository.save(liveLecture);

        // 관리자 승인 DB에 추기
        AdminApproval adminApproval = new AdminApproval(savedLiveLecture);
        adminApprovalRepository.save(adminApproval);

        return "강의 등록 신청이 완료되었습니다. 관리자 승인 후 강의가 활성화됩니다.";
    }

    // 나의 실시간 강의 목록 조회
    @Transactional
    public List<InstructorLiveLectureResponse> getInstructorLiveLectures(Long instructorId) {

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("강사 정보를 찾을 수 없습니다. ID: " + instructorId));

        if (instructor.getRole() != Role.INSTRUCTOR && instructor.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("강사 권한이 있는 사용자만 실시간 강의 목록을 조회할 수 있습니다.");
        }

        return liveLectureRepository.findByInstructorIdOrderByCreatedAtDesc(instructorId).stream()
                .map(InstructorLiveLectureResponse::of)
                .toList();
    }


    // ====== 내부 편의 메서드 =======
    private void validateLiveLectureTime(CreateLiveLectureRequest request) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startAt = request.getStartAt();
        LocalDateTime endAt = request.getEndAt();
        LocalDateTime reservationStartAt = request.getReservationStartAt();

        if (startAt == null) {
            throw new IllegalArgumentException("강의 시작 일시는 필수 입력 값입니다.");
        }

        if (endAt == null) {
            throw new IllegalArgumentException("강의 종료 일시는 필수 입력 값입니다.");
        }

        if (reservationStartAt == null) {
            throw new IllegalArgumentException("예약 시작 일시는 필수 입력 값입니다.");
        }

        if (startAt.isBefore(now)) {
            throw new IllegalArgumentException("강의 시작 일시는 현재 시간 이후여야 합니다.");
        }

        if (!endAt.isAfter(startAt)) {
            throw new IllegalArgumentException("강의 종료 일시는 강의 시작 일시보다 늦어야 합니다.");
        }

        if (reservationStartAt.isBefore(now)) {
            throw new IllegalArgumentException("예약 시작 일시는 현재 시간 이후여야 합니다.");
        }

        if (!reservationStartAt.isBefore(startAt)) {
            throw new IllegalArgumentException("예약 시작 일시는 강의 시작 일시보다 빨라야 합니다.");
        }
    }
}
