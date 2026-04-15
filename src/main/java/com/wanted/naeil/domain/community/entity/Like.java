package com.wanted.naeil.domain.community.entity;

import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes",
        uniqueConstraints = {
        @UniqueConstraint(name = "uk_like_user_post",
                columnNames = {"user_id", "post_id"}),
        @UniqueConstraint(name = "uk_like_user_course",
                columnNames = {"user_id", "course_id"})
        })
@Getter
@NoArgsConstructor
public class Like extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private LikeTargetType targetType;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "course_id")
    private Long courseId;

    // 생성자 + 비즈니스 메서드
    @Builder
    public Like(User user, LikeTargetType targetType, Long postId, Long courseId) {
        this.user = user;
        this.targetType = targetType;
        this.postId = postId;
        this.courseId = courseId;
    }

    public boolean isCanceled() {
        return this.getDeletedAt() != null;
    }

}
