package com.wanted.naeil.domain.admin.repository;

import com.wanted.naeil.domain.admin.entity.enums.ApprovalRequestType;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalStatus;
import com.wanted.naeil.domain.admin.entity.AdminApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminApprovalRepository extends JpaRepository<AdminApproval, Long> {
    List<AdminApproval> findAllByRequestTypeAndStatus(ApprovalRequestType type, ApprovalStatus status);

}
