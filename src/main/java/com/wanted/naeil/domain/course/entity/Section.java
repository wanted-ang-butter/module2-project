package com.wanted.naeil.domain.course.entity;

import com.wanted.naeil.domain.course.entity.enums.SectionStatus;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalTime;

@Entity
@Table(name = "sections")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE sections SET deleted_at = CURRENT_TIMESTAMP WHERE section_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Section extends BaseTimeEntity {

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

    @Column(name = "play_time", nullable = false)
    private LocalTime playTime;

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    @Column(nullable = false)
    private int sequence; // 강의 순서

    @Column(name = "is_free", nullable = false)
    private Boolean isFree;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SectionStatus status;

    // === 비지니스 로직 ===

    @Builder
    public Section(Course course, String title, String videoUrl, LocalTime playTime, String attachmentUrl, int sequence, Boolean isFree, SectionStatus status) {
        this.course = course;
        this.title = title;
        this.videoUrl = videoUrl;
        this.playTime = playTime;
        this.attachmentUrl = attachmentUrl;
        this.sequence = sequence;
        this.status = status;
        this.isFree = (isFree != null) ? isFree : false;
    }

    public void updateSectionInfo(String title, String videoUrl, LocalTime playTime, Boolean isFree, SectionStatus status) {
        this.title = title;
        this.playTime = playTime;
        this.isFree = isFree != null ? isFree : false;
        this.status = status;
    }

    public void updateVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void updateAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
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
