package com.wanted.naeil.domain.admin.repository;

import com.wanted.naeil.domain.admin.entity.ApprovalRequestType;
import com.wanted.naeil.domain.admin.entity.ApprovalStatus;
import com.wanted.naeil.domain.admin.entity.AdminApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseApprovalRepository extends JpaRepository<AdminApproval, Long> {
    List<AdminApproval> findAllByRequestTypeAndStatus(ApprovalRequestType type, ApprovalStatus status);

}
