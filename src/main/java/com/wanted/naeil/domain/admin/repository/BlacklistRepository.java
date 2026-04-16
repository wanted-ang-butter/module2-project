package com.wanted.naeil.domain.admin.repository;

import com.wanted.naeil.domain.admin.entity.BlacklistHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistRepository extends JpaRepository<BlacklistHistory, Long> {
}
