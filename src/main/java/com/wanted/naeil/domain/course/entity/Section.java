package com.wanted.naeil.domain.course.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "sections")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE sections SET deleted_at = CURRENT_TIMESTAMP WHERE section_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "play_time")
    private int playTime;

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    @Column(nullable = false)
    private int sequence; // 강의 순서

    @Column(name = "is_free", nullable = false)
    private Boolean isFree;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SectionStatus status;

    @Builder
    public Section(Course course, String title, String videoUrl, int playTime, String attachmentUrl, int sequence, Boolean isFree) {
        this.course = course;
        this.title = title;
        this.videoUrl = videoUrl;
        this.playTime = playTime;
        this.attachmentUrl = attachmentUrl;
        this.sequence = sequence;
        this.isFree = (isFree != null) ? isFree : false;
    }

    public void updateSequence(int newSequence) {
        this.sequence = newSequence;
    }

    // 섹션 활성화
    public void activate() {
        this.status = SectionStatus.ACTIVE;
    }

    // 섹션 비활성화
    public void deactivate() {
        this.status = SectionStatus.INACTIVE;
    }
}
