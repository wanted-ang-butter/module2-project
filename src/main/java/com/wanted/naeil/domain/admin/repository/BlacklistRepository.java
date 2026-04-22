package com.wanted.naeil.domain.admin.repository;

import com.wanted.naeil.domain.admin.entity.BlacklistHistory;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistRepository extends JpaRepository<BlacklistHistory, Long> {
    @EntityGraph(attributePaths = "user")
    List<BlacklistHistory> findAllByReleaseReasonIsNull();

    @EntityGraph(attributePaths = "user")
    List<BlacklistHistory> findAllByReleaseReasonIsNullOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "user")
    List<BlacklistHistory> findAllByUserIdAndReleaseReasonIsNull(Long userId);

    boolean existsByUserIdAndReleaseReasonIsNull(Long userId);
}
