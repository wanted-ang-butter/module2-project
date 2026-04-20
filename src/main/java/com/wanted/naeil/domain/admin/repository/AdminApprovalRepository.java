package com.wanted.naeil.domain.admin.repository;

import com.wanted.naeil.domain.admin.entity.enums.ApprovalRequestType;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalStatus;
import com.wanted.naeil.domain.admin.entity.AdminApproval;
import com.wanted.naeil.domain.user.entity.InstructorApplications;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminApprovalRepository extends JpaRepository<AdminApproval, Long> {
    List<AdminApproval> findAllByRequestTypeAndStatus(ApprovalRequestType type, ApprovalStatus status);
    // 승재 추가 : 강사 신청 승인 철회 로직
    void deleteByInstructorApplications(InstructorApplications instructorApplications);


    // 정수 추가 : 승인 대기 요청일때 중복 요청 막기
    boolean existsByCourseIdAndRequestTypeAndStatus(
            Long courseId,
            ApprovalRequestType requestType,
            ApprovalStatus status
    );
}
