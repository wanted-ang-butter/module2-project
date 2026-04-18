package com.wanted.naeil.domain.admin.dto.response;

import com.wanted.naeil.domain.admin.entity.enums.ApprovalRequestType;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ApprovalResponse {
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
    private Long liveId;
    private Long applicationId;
    private String applicantName;
    private String introduction;
    private String career;
    private String proofFileUrl;
    private String faceImgUrl;
    private String description;
    private Integer maxCapacity;
    private LocalDateTime startAt;
    private Integer requestedAmount;
    private Integer platformFee;
    private Integer finalAmount;
    private String settlementMonth;
    private List<SettlementDetailInfo> settlementDetails;

    @Getter
    @Builder
    public static class SettlementDetailInfo {
        private String courseName;
        private int saleCount;
        private int totalSalesAmount;
    }



}
