package com.wanted.naeil.domain.live.service;

import com.wanted.naeil.domain.admin.entity.AdminApproval;
import com.wanted.naeil.domain.admin.repository.AdminApprovalRepository;
import com.wanted.naeil.domain.live.dto.request.CreateLiveLectureRequest;
import com.wanted.naeil.domain.live.dto.response.InstructorLiveDetailResponse;
import com.wanted.naeil.domain.live.dto.response.InstructorLiveLectureResponse;
import com.wanted.naeil.domain.live.dto.response.LiveLectureListResponse;
import com.wanted.naeil.domain.live.entity.LiveLecture;
import com.wanted.naeil.domain.live.entity.LiveReservation;
import com.wanted.naeil.domain.live.entity.enums.LiveLectureStatus;
import com.wanted.naeil.domain.live.entity.enums.LiveReservationStatus;
import com.wanted.naeil.domain.live.repository.LiveLectureRepository;
import com.wanted.naeil.domain.live.repository.LiveReservationRepository;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveLectureService {

    private final LiveLectureRepository liveLectureRepository;
    private final LiveReservationRepository liveReservationRepository;
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

        // 필수값 확인 메서드 공동 메서드로 변경
        validateLiveLectureRequiredValues(request);

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
    @Transactional(readOnly = true)
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

    // 강사 - 실시간 강의 상세 조회
    @Transactional(readOnly = true)
    public InstructorLiveDetailResponse getInstructorLiveLectureDetail(Long instructorId, Long liveId) {

        log.info("[실시간 강의] 상세 조회 Service 로직 시작!");

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("강사 정보를 찾을 수 없습니다. ID: " + instructorId));

        LiveLecture liveLecture = liveLectureRepository.findById(liveId)
                .orElseThrow(() -> new IllegalArgumentException("실시간 강의를 찾을 수 없습니다. ID: " + liveId));


        validateLiveLectureOwnerOrAdmin(instructor, liveLecture);

        return InstructorLiveDetailResponse.of(liveLecture);
    }

    // 강사 - 실시간 강의 수정
    @Transactional
    public void updateInstructorLiveLecture(Long instructorId, Long liveId, @Valid CreateLiveLectureRequest request) {

        log.info("[실시간 강의] 실시간 강의 수정 Service 로직 시작!");

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("강사 정보를 찾을 수 없습니다. ID: " + instructorId));

        LiveLecture liveLecture = liveLectureRepository.findById(liveId)
                .orElseThrow(() -> new IllegalArgumentException("실시간 강의를 찾을 수 없습니다. ID: " + liveId));

        // 강사 본인 or 관리자만 처리
        validateLiveLectureOwnerOrAdmin(instructor, liveLecture);

        // 승인 대기 상태일 때만 수정 가능
        LiveLectureStatus status = liveLecture.getStatus();

        if (status != LiveLectureStatus.PENDING) {
            throw new IllegalStateException("승인 대기 상태만 수정할 수 있습니다.");
        }

        // 필수 값 확인
        validateLiveLectureRequiredValues(request);

        // 시간 검증
        validateLiveLectureTime(request);

        liveLecture.update(
                request.getTitle().trim(),
                request.getDescription().trim(),
                request.getMaxCapacity(),
                request.getReservationStartAt(),
                request.getStartAt(),
                request.getEndAt(),
                request.getStreamingUrl().trim()
        );

        log.info("[LiveLectureUpdate] 실시간 강의 수정 완료 - liveId: {}", liveId);
    }

    // 실시간 강의 삭제
    @Transactional
    public void deleteLiveLecture(Long instructorId, Long liveId) {

        log.info("[실시간 강의] 실시간 강의 삭제 Service 로직 시작!");

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("강사 정보를 찾을 수 없습니다. ID: " + instructorId));

        LiveLecture liveLecture = liveLectureRepository.findById(liveId)
                .orElseThrow(() -> new IllegalArgumentException("실시간 강의를 찾을 수 없습니다. ID: " + liveId));

        // 강사 본인 or 관리자
        validateLiveLectureOwnerOrAdmin(instructor, liveLecture);

        // 승인 대기 or 반려 상태일 때만 삭제 가능
        LiveLectureStatus status = liveLecture.getStatus();

        if (status != LiveLectureStatus.PENDING && status != LiveLectureStatus.REJECTED) {
            throw new IllegalStateException("승인 대기 또는 반려 상태의 실시간 강의만 삭제할 수 있습니다.");
        }

        // 관리자 승인 테이블 먼저 삭제
        adminApprovalRepository.deleteByLecture(liveLecture);
        // 실시간 강의 삭제
        liveLectureRepository.delete(liveLecture);

        log.info("[LiveLectureDelete] 실시간 강의 삭제 완료 - liveId: {}", liveId);
    }

    // 실시간 강의 전체 조회 - 유저
    @Transactional(readOnly = true)
    public List<LiveLectureListResponse> getLiveLectureList(Long userId) {

        log.info("[LiveLectureList] 사용자 실시간 강의 전체 조회 시작");

        // 승인완료, 방송 중 강의만 담기
        List<LiveLectureStatus> visibleStatuses = List.of(
                LiveLectureStatus.APPROVED,
                LiveLectureStatus.IN_PROGRESS
        );

        // 종료 전인 실시간 강의 조회
        List<LiveLecture> liveLectures = liveLectureRepository.findByStatusInAndEndAtAfterOrderByStartAtAsc(
                visibleStatuses,
                LocalDateTime.now()
        );

        // 내가 예약한 강의 조회 및 add
        Set<Long> reservedLiveIds = new HashSet<>();

        if (userId != null) {
            List<Long> reservedIds = liveReservationRepository
                    .findLiveIdsByUserIdAndStatus(userId, LiveReservationStatus.RESERVED);
            reservedLiveIds.addAll(reservedIds);
        }

        LocalDateTime now = LocalDateTime.now();

        return liveLectures.stream()
                .map(liveLecture -> toLiveLectureListResponse(
                        liveLecture,
                        reservedLiveIds.contains(liveLecture.getId()), // 내가 예약한 강의인가 확인
                        now
                ))
                .toList();
    }

    // 실시간 강의 예약 기능 - 유저
    @Transactional
    public void reserveLiveLecture(Long userId, Long liveId) {

        log.info("[LiveLectureReserve] 실시간 강의 예약 시작 - userId: {}, liveId: {}", userId, liveId);

        LiveLecture liveLecture = liveLectureRepository.findByIdForUpdate(liveId)
                .orElseThrow(() -> new IllegalArgumentException("실시간 강의를 찾을 수 없습니다. ID: " + liveId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다. ID: " + userId));

        if (user.getRole() != Role.USER) {
            throw new AccessDeniedException("강의를 예약하려면 로그인을 해주세요.");
        }

        boolean alreadyReserved = liveReservationRepository.existsByUser_IdAndLiveLecture_IdAndStatus(
                userId,
                liveId,
                LiveReservationStatus.RESERVED
        );

        if (alreadyReserved) {
            throw new IllegalStateException("이미 예약하신 강의입니다.");
        }

        // 예약 시간 검증
        validateLiveLectureReservable(liveLecture);

        LiveReservation reservation = LiveReservation.builder()
                .user(user)
                .liveLecture(liveLecture)
                .build();

        liveReservationRepository.save(reservation);

        liveLecture.incrementReservation();

        log.info("[LiveLectureReserve] 실시간 강의 예약 완료 - userId: {}, liveId: {}", userId, liveId);
    }


    // ====== 내부 편의 메서드 =======

    // 강의 예약 변환 메서드
    private LiveLectureListResponse toLiveLectureListResponse(
            LiveLecture liveLecture,
            boolean isMyReserved,
            LocalDateTime now
    ) {
        int currentCount = liveLecture.getCurrentCount();
        int maxCapacity = liveLecture.getMaxCapacity();

        int reservationRate = calculateReservationRate(currentCount, maxCapacity);
        boolean isClosingSoon = reservationRate >= 80;
        boolean isLive = isLiveNow(liveLecture.getStartAt(), liveLecture.getEndAt(), now);
        boolean isFull = currentCount >= maxCapacity;
        boolean isEnded = liveLecture.getEndAt() != null && !now.isBefore(liveLecture.getEndAt());

        boolean reservableStatus = liveLecture.getStatus() == LiveLectureStatus.APPROVED
                || liveLecture.getStatus() == LiveLectureStatus.IN_PROGRESS;

        boolean reservable = reservableStatus
                && !isEnded
                && !isFull
                && !isMyReserved;

        return LiveLectureListResponse.of(
                liveLecture,
                reservationRate,
                isClosingSoon,
                isLive,
                isFull,
                reservable,
                isMyReserved
        );
    }


    // 실시간 강의 등록 시간값 검증
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

    // 실시간 강의 필수 값 검증
    private void validateLiveLectureRequiredValues(CreateLiveLectureRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("실시간 강의 제목은 필수 입력 값입니다.");
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("실시간 강의 설명은 필수 입력 값입니다.");
        }

        if (request.getMaxCapacity() == null || request.getMaxCapacity() < 1) {
            throw new IllegalArgumentException("수강 정원은 1명 이상이어야 합니다.");
        }

        if (request.getMaxCapacity() > 100) {
            throw new IllegalArgumentException("신청 가능한 최대 수강 정원은 100명입니다.");
        }

        if (request.getStreamingUrl() == null || request.getStreamingUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("방송 URL은 필수 입력 값입니다.");
        }
    }

    // 본인 강의 검증
    private void validateLiveLectureOwnerOrAdmin(User user, LiveLecture liveLecture) {
        if (user.getRole() == Role.ADMIN) {
            return;
        }

        if (user.getRole() != Role.INSTRUCTOR) {
            throw new AccessDeniedException("강사 또는 관리자만 실시간 강의를 조회할 수 있습니다.");
        }

        if (!liveLecture.getInstructor().getId().equals(user.getId())) {
            throw new AccessDeniedException("본인이 등록한 실시간 강의만 조회할 수 있습니다.");
        }
    }

    // 실시간 강의 예약 검증
    private void validateLiveLectureReservable(LiveLecture liveLecture) {

        LocalDateTime now = LocalDateTime.now();

        LiveLectureStatus status = liveLecture.getStatus();

        if (status != LiveLectureStatus.APPROVED && status != LiveLectureStatus.IN_PROGRESS) {
            throw new IllegalStateException("예약 가능한 실시간 강의가 아닙니다.");
        }

        if (liveLecture.getEndAt() == null || !now.isBefore(liveLecture.getEndAt())) {
            throw new IllegalStateException("이미 종료된 실시간 강의는 예약할 수 없습니다.");
        }

        if (liveLecture.getCurrentCount() >= liveLecture.getMaxCapacity()) {
            throw new IllegalStateException("정원이 초과되어 예약이 마감되었습니다.");
        }
    }

    // 예약률 계산
    private int calculateReservationRate(int currentCount, int maxCapacity) {
        if (maxCapacity <= 0) {
            return 0;
        }
        return currentCount * 100 / maxCapacity;
    }

    // 실시간 강의 여부 검증
    private boolean isLiveNow(LocalDateTime startAt, LocalDateTime endAt, LocalDateTime now) {
        if (startAt == null || endAt == null) {
            return false;
        }

        return !now.isBefore(startAt) && now.isBefore(endAt);
    }
}
