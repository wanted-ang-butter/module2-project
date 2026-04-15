package com.wanted.naeil.domain.admin.dto.response;

import com.wanted.naeil.domain.admin.entity.ApprovalRequestType;
import com.wanted.naeil.domain.admin.entity.ApprovalStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CourseApprovalResponse {
    private Long approvalId;
    private ApprovalRequestType requestType;
    private ApprovalStatus status;
    private LocalDateTime createdAt;
    private Long courseId;
    private String title;
    private Integer price;
    private String thumbnail;
    private String categoryName;
    private String instuctorName;


}
