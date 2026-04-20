package com.wanted.naeil.domain.user.dto.response;

import com.wanted.naeil.domain.user.entity.InstructorApplications;
import com.wanted.naeil.domain.user.entity.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InstructorApplicationResponse {
    private Long id;
    private String title;
    private String categoryName;
    private ApplicationStatus status;
    private String rejectReason;
    private LocalDateTime createdAt;

    public static InstructorApplicationResponse from(InstructorApplications application) {
        return InstructorApplicationResponse.builder()
                .id(application.getId())
                .title(application.getTitle())
                .categoryName(application.getCategory().getName())
                .status(application.getStatus())
                .rejectReason(application.getRejectReason())
                .createdAt(application.getCreatedAt())
                .build();
    }
}
