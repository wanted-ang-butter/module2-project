package com.wanted.naeil.domain.admin.service;

import com.wanted.naeil.domain.admin.dto.response.CourseApprovalResponse;
import com.wanted.naeil.domain.admin.entity.ApprovalRequestType;
import com.wanted.naeil.domain.admin.entity.ApprovalStatus;
import com.wanted.naeil.domain.admin.entity.CourseApproval;
import com.wanted.naeil.domain.admin.repository.CourseApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service   // 스프링한테 서비스 클래스라고 알려줘야쥐잉
@RequiredArgsConstructor //service에서 Repository 쓰야하니까 필드선언
public class AdminCourseApprovalService {
    private final CourseApprovalRepository courseApprovalRepository;
    // @RequiredArgsConstructor 있으면 final 필드보고 원래
    //public AdminCourseApprovalService(CourseApprovalRepository courseApprovalRepository) {
    //    this.courseApprovalRepository = courseApprovalRepository;
    // 이렇게 길게 써야하는걸 final + @RequiredArgsConstructor 이거써서 자동 생성
    public List<CourseApprovalRepository> getApprovals(ApprovalRequestType){
        List<CourseApproval>approvals = courseApprovalRepository.findAllByRequestTypeAndStatus(type, ApprovalStatus.PENDING);
        return approvals.stream()
                .map(approval -> CourseApprovalResponse.builder()
                        .approvalId()
                        .title()
                        .build())
                .collect(Collectors.toList());

    }
}
