package com.wanted.naeil.domain.user.repository;

import com.wanted.naeil.domain.user.entity.InstructorApplications;
import com.wanted.naeil.domain.user.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsturctorApplicationRepository extends JpaRepository<InstructorApplications, Long> {
    List<InstructorApplications> findByUser_UsernameOrderByCreatedAtDesc(String username);
    boolean existsByUser_UsernameAndStatus(String username, ApplicationStatus status);
}
