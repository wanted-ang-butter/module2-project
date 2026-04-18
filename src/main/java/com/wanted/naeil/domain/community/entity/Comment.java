package com.wanted.naeil.domain.community.entity;

import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE comments SET deleted_at = NOW() WHERE comment_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    // 양방향 - Post의 comments 리스트와 연결
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder
    public Comment(Post post, User user, String content) {
        this.post = post;
        this.user = user;
        this.content = content;
    }

    // ==== 비즈니스 메서드 ====

    public void update(String content) {
        this.content = content;
    }
}
