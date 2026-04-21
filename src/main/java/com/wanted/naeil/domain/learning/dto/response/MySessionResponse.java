package com.wanted.naeil.domain.learning.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MySessionResponse {

    private Long sectionId;
    private Integer sequence;
    private String title;
    private String videoUrl;
    private String playTime;
    private String attachmentUrl;
    private boolean completed;
}