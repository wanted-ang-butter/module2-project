package com.wanted.naeil.domain.course.entity;

import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE reviews SET deleted_at = CURRENT_TIMESTAMP WHERE review_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private double rating; // 1~5점

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder
    public Review(Course course, User user, Double rating, String content) {
        this.course = course;
        this.user = user;
        this.rating = rating;
        this.content = content;
    }

    public void update(Double rating, String content) {
        this.rating = rating;
        this.content = content;
    }
}
