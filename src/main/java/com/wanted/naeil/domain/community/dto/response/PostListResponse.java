package com.wanted.naeil.domain.community.dto.response;

import com.wanted.naeil.domain.community.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostListResponse {

    private Long postId;
    private String title;
    private String nickname;
    private LocalDateTime createdAt;
    private int viewCount;
    private long likeCount;

    public static PostListResponse from(Post post, long likeCount) {
        return PostListResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .nickname(post.getUser().getNickname())
                .createdAt(post.getCreatedAt())
                .viewCount(post.getViewCount())
                .likeCount(likeCount)
                .build();
    }
}
