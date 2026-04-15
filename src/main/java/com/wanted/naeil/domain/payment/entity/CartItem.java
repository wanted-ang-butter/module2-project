package com.wanted.naeil.domain.payment.entity;

import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
// 중복 결제 방지를 위한 unique 설정
@Table(
        name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cart_user_course", columnNames = {"user_id", "course_id"})
        }
)
@Getter
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    public CartItem(User user, Course course) {
        this.user = user;
        this.course = course;
        this.addedAt = LocalDateTime.now();
    }
}
