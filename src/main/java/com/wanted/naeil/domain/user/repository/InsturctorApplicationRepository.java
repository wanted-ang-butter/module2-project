package com.wanted.naeil.domain.user.repository;

import com.wanted.naeil.domain.user.entity.InstructorApplications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsturctorApplicationRepository extends JpaRepository<InstructorApplications, Long> {
}
