package com.wanted.naeil.domain.community.dto.request;

import com.wanted.naeil.domain.community.entity.enums.PostCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostCreateRequest {

    private PostCategory category;
    private String title;
    private String content;
    private Boolean isPublic;
    private Long courseId;
}