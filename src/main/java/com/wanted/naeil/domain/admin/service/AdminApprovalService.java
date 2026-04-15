package com.wanted.naeil.domain.admin.service;

import com.wanted.naeil.domain.admin.dto.response.ApprovalResponse;
import com.wanted.naeil.domain.admin.entity.ApprovalRequestType;
import com.wanted.naeil.domain.admin.entity.ApprovalStatus;
import com.wanted.naeil.domain.admin.entity.CourseApproval;
import com.wanted.naeil.domain.admin.repository.AdminApprovalRepository;
import com.wanted.naeil.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service   // 스프링한테 서비스 클래스라고 알려줘야쥐잉
@RequiredArgsConstructor //service에서 Repository 쓰야하니까 필드선언
public class AdminApprovalService {
    private final AdminApprovalRepository courseApprovalRepository;
    // @RequiredArgsConstructor 있으면 final 필드보고 원래
    //public AdminCourseApprovalService(CourseApprovalRepository courseApprovalRepository) {
    //    this.courseApprovalRepository = courseApprovalRepository;
    // 이렇게 길게 써야하는걸 final + @RequiredArgsConstructor 이거써서 자동 생성
    // 1. 목록조회
    public List<ApprovalResponse> getApprovals(ApprovalRequestType type){

        List<CourseApproval>approvals = courseApprovalRepository.findAllByRequestTypeAndStatus(type, ApprovalStatus.PENDING);

        return approvals.stream()
                .map(approval -> ApprovalResponse.builder()
                        .approvalId(approval.getApprovalId())
                        .requestType(approval.getRequestType())
                        .status(approval.getStatus())
                        .createdAt(approval.getCreatedAt())
                        .courseId(approval.getCourse().getId())
                        .title(approval.getCourse().getTitle())
                        .price(approval.getCourse().getPrice())
                        .thumbnail(approval.getCourse().getThumbnail())
                        .categoryName(approval.getCourse().getCategory().getName())
                        .instuctorName(approval.getCourse().getInstructor().getName())
                        .build())
                .collect(Collectors.toList());
    }

    // 2. 승인 처리
    public void approve(Long approvalId, User admin) {
        // 1) 승인건 찾아오기
        CourseApproval approval = courseApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("승인 건을 찾을수 없습니다"));
        // .orElseThrow = 값이 없으면 예외 던지라는거여
        // 2) 승인 처리
        approval.approve((admin));
        // 3) 코스 활성화
        approval.getCourse().activate();
    }

    // 3. 반려 처리
    public  void reject(Long approvalId , User admin , String rejectReason) {
        // 1) 반려건 찾아오기
        CourseApproval approval =  courseApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("반려 건을 찾을수 없습니다"));
        // 2) 반려 처리
        approval.reject(admin , rejectReason);


    }


}
