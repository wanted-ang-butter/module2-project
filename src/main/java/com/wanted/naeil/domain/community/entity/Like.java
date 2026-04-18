package com.wanted.naeil.domain.community.entity;

import com.wanted.naeil.domain.community.entity.enums.LikeTargetType;
import com.wanted.naeil.domain.course.entity.Course;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = true)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = true)
    private Course course;

    // 생성자 + 비즈니스 메서드
    @Builder
    public Like(User user, LikeTargetType targetType, Post post, Course course) {
        this.user = user;
        this.targetType = targetType;
        this.post = post;
        this.course = course;
    }

    public boolean isCanceled() {
        return this.getDeletedAt() != null;
    }

}