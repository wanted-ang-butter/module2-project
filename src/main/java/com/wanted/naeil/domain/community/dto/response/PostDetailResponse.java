package com.wanted.naeil.domain.community.dto.response;

import com.wanted.naeil.domain.community.entity.Post;
import com.wanted.naeil.domain.community.entity.enums.PostCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostDetailResponse {

    private Long postId;
    private String title;
    private String content;
    private String nickname;
    private PostCategory category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int viewCount;
    private long likeCount;
    private boolean isResolved;
    private boolean isPublic;
    private boolean isLiked;
    private Long likeId;
    private List<CommentResponse> comments;
    private Long courseId;
    private String courseTitle;

    public static PostDetailResponse from(Post post, long likeCount,
                                            boolean isLiked, Long likeId,
                                          List<CommentResponse> comments) {
        return PostDetailResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .nickname(post.getUser().getNickname())
                .category(post.getCategory())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .viewCount(post.getViewCount())
                .likeCount(likeCount)
                .isResolved(post.isResolved())
                .isPublic(post.isPublic())
                .isLiked(isLiked)
                .likeId(likeId)
                .comments(comments)
                .courseId(post.getCourse() != null ? post.getCourse().getId() : null)
                .courseTitle(post.getCourse() != null ? post.getCourse().getTitle() : null)
                .build();
    }
}
