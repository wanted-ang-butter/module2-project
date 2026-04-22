package com.wanted.naeil.domain.admin.service;

import com.wanted.naeil.domain.admin.dto.response.ApprovalResponse;
import com.wanted.naeil.domain.admin.entity.AdminApproval;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalRequestType;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalStatus;
import com.wanted.naeil.domain.admin.repository.AdminApprovalRepository;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import com.wanted.naeil.domain.live.entity.LiveLecture;
import com.wanted.naeil.domain.live.entity.enums.LiveLectureStatus;
import com.wanted.naeil.domain.live.repository.LiveLectureRepository;
import com.wanted.naeil.domain.settlement.entity.Settlement;
import com.wanted.naeil.domain.user.entity.InstructorApplications;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.entity.enums.Role;
import com.wanted.naeil.domain.user.repository.InsturctorApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AdminApprovalService {
    private final AdminApprovalRepository courseApprovalRepository;
    private final CourseRepository courseRepository;
    private final InsturctorApplicationRepository insturctorApplicationRepository;
    private final LiveLectureRepository liveLectureRepository;

    @Transactional(readOnly = true)
    public List<ApprovalResponse> getApprovals(ApprovalRequestType type) {
        return loadApprovals(type).stream()
                .map(this::toApprovalResponse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApprovalResponse> getPendingApprovals(ApprovalRequestType type) {
        return loadApprovals(type, ApprovalStatus.PENDING).stream()
                .map(this::toApprovalResponse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<AdminApproval> loadApprovals(ApprovalRequestType type) {
        return switch (type) {
            case COURSE_REGISTER, COURSE_DELETE ->
                    courseApprovalRepository.findCourseApprovalsWithCourse(type);
            case INSTRUCTOR_REGISTER ->
                    courseApprovalRepository.findInstructorApprovalsWithDetails(type);
            case LIVE_REGISTER ->
                    courseApprovalRepository.findLiveApprovalsWithInstructor(type);
            case SETTLEMENT_REGISTER ->
                    courseApprovalRepository.findSettlementApprovalsWithDetails(type);
        };
    }

    private List<AdminApproval> loadApprovals(ApprovalRequestType type, ApprovalStatus status) {
        return switch (type) {
            case COURSE_REGISTER, COURSE_DELETE ->
                    courseApprovalRepository.findCourseApprovalsWithCourse(type, status);
            case INSTRUCTOR_REGISTER ->
                    courseApprovalRepository.findInstructorApprovalsWithDetails(type, status);
            case LIVE_REGISTER ->
                    courseApprovalRepository.findLiveApprovalsWithInstructor(type, status);
            case SETTLEMENT_REGISTER ->
                    courseApprovalRepository.findSettlementApprovalsWithDetails(type, status);
        };
    }

    private ApprovalResponse toApprovalResponse(AdminApproval approval) {
        ApprovalResponse.ApprovalResponseBuilder builder = ApprovalResponse.builder()
                .approvalId(approval.getApprovalId())
                .requestType(approval.getRequestType())
                .status(approval.getStatus())
                .createdAt(approval.getCreatedAt());

        switch (approval.getRequestType()) {
            case COURSE_REGISTER, COURSE_DELETE -> {
                Course course = approval.getCourse();
                if (course == null) {
                    return null;
                }
                builder.courseId(course.getId())
                        .title(course.getTitle())
                        .price(course.getPrice())
                        .thumbnail(course.getThumbnail())
                        .categoryName(course.getCategory().getName())
                        .instuctorName(course.getInstructor().getName());
            }
            case INSTRUCTOR_REGISTER -> {
                InstructorApplications applications = approval.getInstructorApplications();
                if (applications == null) {
                    return null;
                }
                builder.applicationId(applications.getId())
                        .applicantName(applications.getUser().getName())
                        .role(applications.getUser().getRole())
                        .title(applications.getTitle())
                        .categoryName(applications.getCategory().getName())
                        .introduction(applications.getIntroduction())
                        .career(applications.getCareer())
                        .proofFileUrl(applications.getProofFileUrl())
                        .faceImgUrl(applications.getFaceImgUrl());
            }
            case LIVE_REGISTER -> {
                LiveLecture lecture = approval.getLecture();
                if (lecture == null) {
                    return null;
                }
                builder.liveId(lecture.getId())
                        .title(lecture.getTitle())
                        .description(lecture.getDescription())
                        .maxCapacity(lecture.getMaxCapacity())
                        .startAt(lecture.getStartAt())
                        .instuctorName(lecture.getInstructor().getName());
            }
            case SETTLEMENT_REGISTER -> {
                Settlement settlement = approval.getSettlement();
                if (settlement == null) {
                    return null;
                }
                builder.instuctorName(settlement.getInstructor().getName())
                        .requestedAmount(settlement.getRequestedAmount())
                        .platformFee(settlement.getPlatformFee())
                        .finalAmount(settlement.getFinalAmount())
                        .settlementMonth(settlement.getSettlementMonth())
                        .settlementDetails(settlement.getDetails().stream()
                                .map(detail -> ApprovalResponse.SettlementDetailInfo.builder()
                                        .courseName(detail.getCourse().getTitle())
                                        .saleCount(detail.getSaleCount())
                                        .totalSalesAmount(detail.getTotalSalesAmount())
                                        .build())
                                .collect(Collectors.toList()));
            }
        }

        return builder.build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void approve(Long approvalId, User admin) {
        AdminApproval approval = courseApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new NoSuchElementException("Approval not found."));

        approval.approve(admin);

        switch (approval.getRequestType()) {
            case COURSE_REGISTER -> approval.getCourse().activate();
            case COURSE_DELETE -> courseRepository.delete(approval.getCourse());
            case INSTRUCTOR_REGISTER -> {
                approval.getInstructorApplications().approve();
                approval.getInstructorApplications().getUser().changeRole(Role.INSTRUCTOR);
                insturctorApplicationRepository.save(approval.getInstructorApplications());
            }
            case LIVE_REGISTER -> {
                approval.getLecture().changeStatus(LiveLectureStatus.APPROVED);
                liveLectureRepository.save(approval.getLecture());
            }
            case SETTLEMENT_REGISTER -> approval.getSettlement().approve(admin);
        }
        courseApprovalRepository.save(approval);
    }
    //  반려 처리
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void reject(Long approvalId, User admin, String rejectReason) {
        AdminApproval approval = courseApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new NoSuchElementException("Approval not found."));

        approval.reject(admin, rejectReason);

        switch (approval.getRequestType()) {
            case COURSE_REGISTER -> approval.getCourse().rejectRegistration();
            case COURSE_DELETE -> {
                // Keep the course in its current inactive state when a delete request is rejected.
            }
            case INSTRUCTOR_REGISTER -> {
                approval.getInstructorApplications().reject(rejectReason);
                insturctorApplicationRepository.save(approval.getInstructorApplications());
            }
            case LIVE_REGISTER -> {
                approval.getLecture().changeStatus(LiveLectureStatus.REJECTED);
                liveLectureRepository.save(approval.getLecture());
            }
            case SETTLEMENT_REGISTER -> approval.getSettlement().reject(admin);
        }
        courseApprovalRepository.save(approval);
    }
}
