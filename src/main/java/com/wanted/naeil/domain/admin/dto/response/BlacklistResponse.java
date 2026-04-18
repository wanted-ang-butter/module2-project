package com.wanted.naeil.domain.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistResponse {

    Long blacklistID;
    Long userId;
    String userName;
    String reason;
    String releaseReason;
    LocalDateTime createdAt;
}
