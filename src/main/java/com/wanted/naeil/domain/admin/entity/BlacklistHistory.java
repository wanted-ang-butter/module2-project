package com.wanted.naeil.domain.admin.entity;

import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "blacklist_history")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blacklist_id")
    private Long blacklistId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "release_reason", columnDefinition = "TEXT")
    private String releaseReason;
}
