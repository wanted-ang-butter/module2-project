package com.wanted.naeil.domain.admin.service;

import com.wanted.naeil.domain.admin.dto.response.ApprovalResponse;
import com.wanted.naeil.domain.admin.entity.AdminApproval;
import com.wanted.naeil.domain.admin.entity.ApprovalRequestType;
import com.wanted.naeil.domain.admin.entity.ApprovalStatus;
import com.wanted.naeil.domain.admin.repository.AdminApprovalRepository;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.live.entity.LiveLecture;
import com.wanted.naeil.domain.live.entity.LiveLectureStatus;
import com.wanted.naeil.domain.live.repository.LiveLectureRepository;
import com.wanted.naeil.domain.user.entity.InstructorApplications;
import com.wanted.naeil.domain.user.entity.Role;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.InsturctorApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service   // 스프링한테 서비스 클래스라고 알려줘야쥐잉
@RequiredArgsConstructor //service에서 Repository 쓰야하니까 필드선언
public class AdminApprovalService {
    private final AdminApprovalRepository courseApprovalRepository;
    private final InsturctorApplicationRepository insturctorApplicationRepository;
    private final LiveLectureRepository liveLectureRepository;

    // 1. 승인 목록조회
    @Transactional
    public List<ApprovalResponse> getApprovals(ApprovalRequestType type){

        List<AdminApproval>approvals = courseApprovalRepository.findAllByRequestTypeAndStatus(type, ApprovalStatus.PENDING);

        return approvals.stream()
                .map(approval -> {
                    ApprovalResponse.ApprovalResponseBuilder builder =
                    ApprovalResponse.builder()
                            .approvalId(approval.getApprovalId())
                            .requestType(approval.getRequestType())
                            .status(approval.getStatus())
                            .createdAt(approval.getCreatedAt());
                    switch (approval.getRequestType()) {
                        case COURSE_REGISTER , COURSE_DELETE -> {
                            Course course = approval.getCourse();
                            builder.courseId(course.getId())
                                    .title(course.getTitle())
                                    .price(course.getPrice())
                                    .thumbnail(course.getThumbnail())
                                    .categoryName(course.getCategory().getName())
                                    .instuctorName(course.getInstructor().getName());
                        }
                        case INSTRUCTOR_REGISTER -> {
                            InstructorApplications applications =
                                    approval.getInstructorApplications();
                            builder.applicationId(applications.getId())
                                    .applicantName(applications.getUser().getName())
                                    .title(applications.getTitle())
                                    .categoryName(applications.getCategory().getName())
                                    .introduction(applications.getIntroduction())
                                    .career(applications.getCareer())
                                    .proofFileUrl(applications.getProofFileUrl())
                                    .faceImgUrl(applications.getFaceImgUrl());
                        }
                        case LIVE_REGISTER -> {
                            LiveLecture lecture = approval.getLecture();
                            builder.liveId((lecture.getId()))
                                    .title(lecture.getTitle())
                                    .description(lecture.getDescription())
                                    .maxCapacity(lecture.getMaxCapacity())
                                    .startAt(lecture.getStartAt())
                                    .instuctorName(lecture.getInstructor().getName());
                        }
                    }
                    return builder.build();
                })
                .collect(Collectors.toList());
        }
    // 2. 승인 처리
    @Transactional
    public void approve(Long approvalId, User admin) {
        // 1) 승인건 찾아오기
        AdminApproval approval = courseApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new NoSuchElementException("승인 건을 찾을수 없습니다"));
        // 2) 승인 처리
        approval.approve(admin);
        // 3) type별 후속 처리
        switch (approval.getRequestType()) {
            case COURSE_REGISTER -> {
                approval.getCourse().activate();
            }
            case  COURSE_DELETE -> {
                approval.getCourse().deactivate();
            }
            case INSTRUCTOR_REGISTER -> {
                approval.getInstructorApplications().approve();
                approval.getInstructorApplications().getUser().changeRole(Role.INSTRUCTOR);
                insturctorApplicationRepository.save(approval.getInstructorApplications());
            }
            case LIVE_REGISTER -> {
                approval.getLecture().changeStatus(LiveLectureStatus.APPROVED);
                liveLectureRepository.save(approval.getLecture());
            }

        }
        courseApprovalRepository.save(approval);
    }
    // 3. 반려 처리
    @Transactional
    public  void reject(Long approvalId , User admin , String rejectReason) {
        // 1) 반려건 찾아오기
        AdminApproval approval =  courseApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new NoSuchElementException("반려 건을 찾을수 없습니다"));
        // 2) 반려 처리
        approval.reject(admin, rejectReason);

        switch (approval.getRequestType()) {
            case COURSE_REGISTER, COURSE_DELETE -> {
            }
            case INSTRUCTOR_REGISTER -> {
                approval.getInstructorApplications().reject(rejectReason);
                insturctorApplicationRepository.save(approval.getInstructorApplications());
            }
            case LIVE_REGISTER -> {
                approval.getLecture().changeStatus(LiveLectureStatus.REJECTED);
                liveLectureRepository.save(approval.getLecture());
            }
        }
        courseApprovalRepository.save(approval);

    }
}
