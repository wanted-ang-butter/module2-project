package com.wanted.naeil.domain.community.entity;

import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "course_id")
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private PostCategory category;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column(name = "is_resolved", nullable = false)
    private boolean isResolved = false;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = true;

    // 댓글은 CommentRepository에서 따로 조회

    @Builder
    public  Post(User user, Long courseId, PostCategory category,
                 String title, String content, boolean isPublic) {
        this.user = user;
        this.courseId = courseId;
        this.category = category;
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
    }

    // ==== 비즈니스 메서드 ====

    public void update(String title, String content, boolean isPublic) {
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
    }

    public void toggleResolved() {
        this.isResolved = !this.isResolved;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public boolean isDeleted() {
        return this.getDeletedAt() != null;
    }
}
