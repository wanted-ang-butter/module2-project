package com.wanted.naeil.domain.admin.dto.response;

import java.time.LocalDateTime;

public class BlacklistResponse {

    Long blacklistID;
    Long userId;
    String userName;
    String reason;
    String releaseReason;
    LocalDateTime createdAt;
}
