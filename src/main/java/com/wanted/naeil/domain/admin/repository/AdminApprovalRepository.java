package com.wanted.naeil.domain.admin.repository;

import com.wanted.naeil.domain.admin.entity.ApprovalRequestType;
import com.wanted.naeil.domain.admin.entity.ApprovalStatus;
import com.wanted.naeil.domain.admin.entity.CourseApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminApprovalRepository extends JpaRepository<CourseApproval, Long> {
    List<CourseApproval> findAllByRequestTypeAndStatus(ApprovalRequestType type, ApprovalStatus status);

}
