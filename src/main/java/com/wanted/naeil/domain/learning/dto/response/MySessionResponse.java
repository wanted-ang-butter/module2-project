package com.wanted.naeil.domain.learning.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MySessionResponse {

    private Long sessionId;
    private Integer sessionOrder;
    private String title;
    private Integer playTime;
    private boolean completed;
}