package com.wanted.naeil.domain.course.entity;

import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE courses SET deleted_at = CURRENT_TIMESTAMP WHERE course_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Course extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CourseStatus status;

    // 양방향 매핑 (필요 시)
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    // 비지니스 로직

    @Builder
    public Course(User instructor, Category category, String title, String description, int price, String thumbnail) {
        this.instructor = instructor;
        this.category = category;
        this.title = title;
        this.description = description;
        this.price = price;
        this.thumbnail = thumbnail;
        this.status = CourseStatus.PENDING; // 초기 생성 시 승인 대기 상태
    }

    // 강의 정보 수정
    public void updateBasicInfo(String title, Category category, String description, int price, String thumbnail) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
        this.thumbnail = thumbnail;
    }

    // 비즈니스 로직
    public void activate() {
        this.status = CourseStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = CourseStatus.INACTIVE;
    }
}
