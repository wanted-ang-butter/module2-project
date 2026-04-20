package com.wanted.naeil.domain.admin.repository;

import com.wanted.naeil.domain.admin.entity.enums.ApprovalRequestType;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalStatus;
import com.wanted.naeil.domain.admin.entity.AdminApproval;
import com.wanted.naeil.domain.live.entity.LiveLecture;
import com.wanted.naeil.domain.user.entity.InstructorApplications;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminApprovalRepository extends JpaRepository<AdminApproval, Long> {
    List<AdminApproval> findAllByRequestTypeAndStatus(ApprovalRequestType type, ApprovalStatus status);

    // 정수 추가 : 승인 대기 요청일때 중복 요청 막기
    boolean existsByCourseIdAndRequestTypeAndStatus(
            Long courseId,
            ApprovalRequestType requestType,
            ApprovalStatus status
    );

    // 승재 추가 : 강사 스스로 강사 신청 철회
    void deleteByInstructorApplications(InstructorApplications instructorApplications);

    // 정수 추가 : 실시간 강의 삭제
    void deleteByLecture(LiveLecture lecture);

    // 정수 추가 : 강사가 등록 요청 취소 했을 때, 승인 테이블에 기록 삭제하기 위한 코드
    void deleteByCourseIdAndRequestTypeAndStatus(
            Long courseId,
            ApprovalRequestType requestType,
            ApprovalStatus status
    );
}
