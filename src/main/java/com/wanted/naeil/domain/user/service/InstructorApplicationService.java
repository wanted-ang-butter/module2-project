package com.wanted.naeil.domain.user.service;

import com.wanted.naeil.domain.admin.entity.AdminApproval;
import com.wanted.naeil.domain.admin.repository.AdminApprovalRepository;
import com.wanted.naeil.domain.course.entity.Category;
import com.wanted.naeil.domain.course.repository.CategoryRepository;
import com.wanted.naeil.domain.user.dto.request.ApplyInstructorRequest;
import com.wanted.naeil.domain.user.dto.response.InstructorApplicationResponse;
import com.wanted.naeil.domain.user.entity.InstructorApplications;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.entity.enums.ApplicationStatus;
import com.wanted.naeil.domain.user.repository.InsturctorApplicationRepository;
import com.wanted.naeil.domain.user.repository.UserRepository;
import com.wanted.naeil.global.util.file.LocalFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorApplicationService {

    private final InsturctorApplicationRepository applicationRepository;
    private final AdminApprovalRepository adminApprovalRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocalFileService localFileService;

    // 강사 신청 내역 조회
    @Transactional(readOnly = true)
    public List<InstructorApplicationResponse> getMyApplications(String username) {
        return applicationRepository.findByUser_UsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(InstructorApplicationResponse::from)
                .collect(Collectors.toList());
    }

    // 강사 신청 제출
    @Transactional
    public void apply(String username, ApplyInstructorRequest request) {

        // 1. 필수값 검증
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("제목을 입력해주세요.");
        }
        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("분야를 선택해주세요.");
        }
        if (request.getIntroduction() == null || request.getIntroduction().isBlank()) {
            throw new IllegalArgumentException("소개를 입력해주세요.");
        }
        if (request.getCareer() == null || request.getCareer().isBlank()) {
            throw new IllegalArgumentException("경력을 입력해주세요.");
        }
        if (request.getAccountNumber() == null || request.getAccountNumber().isBlank()) {
            throw new IllegalArgumentException("계좌번호를 입력해주세요.");
        }
        if (request.getProofFiles() == null || request.getProofFiles().stream().allMatch(f -> f == null || f.isEmpty())) {
            throw new IllegalArgumentException("증빙 자료를 최소 1개 이상 업로드해주세요.");
        }
        if (request.getFaceImg() == null || request.getFaceImg().isEmpty()) {
            throw new IllegalArgumentException("얼굴 사진을 업로드해주세요.");
        }

        // 1. 중복 신청 방지
        if (applicationRepository.existsByUser_UsernameAndStatus(username, ApplicationStatus.PENDING)) {
            throw new IllegalStateException("이미 승인 대기 중인 신청이 있습니다.");
        }

        // 2. 유저, 카테고리 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("카테고리를 찾을 수 없습니다."));

        // 3. 파일 업로드
        String proofFileUrl = null;
        String faceImgUrl = null;

        if (request.getProofFiles() != null && !request.getProofFiles().isEmpty()) {
            List<String> proofUrls = request.getProofFiles().stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .map(file -> localFileService.uploadSingleFile(file, "instructor/proof"))
                    .collect(Collectors.toList());
            if (!proofUrls.isEmpty()) {
                proofFileUrl = String.join(",", proofUrls);
            }
        }

        if (request.getFaceImg() != null && !request.getFaceImg().isEmpty()) {
            faceImgUrl = localFileService.uploadSingleFile(request.getFaceImg(), "instructor/face");
        }

        // 4. InstructorApplications 저장
        InstructorApplications application = InstructorApplications.builder()
                .user(user)
                .category(category)
                .title(request.getTitle())
                .introduction(request.getIntroduction())
                .career(request.getCareer())
                .accountNumber(request.getAccountNumber())
                .proofFileUrl(proofFileUrl)
                .faceImgUrl(faceImgUrl)
                .build();
        applicationRepository.save(application);

        // 5. AdminApproval 생성 → 팀원 로직과 연결
        AdminApproval approval = new AdminApproval(application);
        adminApprovalRepository.save(approval);
    }

    // 강사 신청 철회
    @Transactional
    public void cancel(String username, Long applicationId) {
        InstructorApplications application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NoSuchElementException("신청 내역을 찾을 수 없습니다."));

        if (!application.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("본인의 신청만 철회할 수 있습니다.");
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("대기 중인 신청만 철회할 수 있습니다.");
        }

        // AdminApproval 먼저 삭제 후 신청 삭제
        adminApprovalRepository.deleteByInstructorApplications(application);
        applicationRepository.delete(application);
    }
}