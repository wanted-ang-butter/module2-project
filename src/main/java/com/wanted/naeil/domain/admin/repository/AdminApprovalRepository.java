package com.wanted.naeil.domain.admin.repository;

import com.wanted.naeil.domain.admin.entity.AdminApproval;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalRequestType;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalStatus;
import com.wanted.naeil.domain.live.entity.LiveLecture;
import com.wanted.naeil.domain.user.entity.InstructorApplications;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminApprovalRepository extends JpaRepository<AdminApproval, Long> {
    List<AdminApproval> findAllByRequestTypeAndStatus(ApprovalRequestType type, ApprovalStatus status);

    @Query("""
            select approval
            from AdminApproval approval
            join fetch approval.course course
            join fetch course.category
            join fetch course.instructor
            where approval.requestType = :type
              and approval.status = :status
            order by approval.createdAt desc
            """)
    List<AdminApproval> findCourseApprovalsWithCourse(
            @Param("type") ApprovalRequestType type,
            @Param("status") ApprovalStatus status
    );

    @Query("""
            select approval
            from AdminApproval approval
            join fetch approval.course course
            join fetch course.category
            join fetch course.instructor
            where approval.requestType = :type
            order by approval.createdAt desc
            """)
    List<AdminApproval> findCourseApprovalsWithCourse(@Param("type") ApprovalRequestType type);

    @Query("""
            select approval
            from AdminApproval approval
            join fetch approval.instructorApplications applications
            join fetch applications.user
            join fetch applications.category
            where approval.requestType = :type
              and approval.status = :status
            order by approval.createdAt desc
            """)
    List<AdminApproval> findInstructorApprovalsWithDetails(
            @Param("type") ApprovalRequestType type,
            @Param("status") ApprovalStatus status
    );

    @Query("""
            select approval
            from AdminApproval approval
            join fetch approval.instructorApplications applications
            join fetch applications.user
            join fetch applications.category
            where approval.requestType = :type
            order by approval.createdAt desc
            """)
    List<AdminApproval> findInstructorApprovalsWithDetails(@Param("type") ApprovalRequestType type);

    @Query("""
            select approval
            from AdminApproval approval
            join fetch approval.lecture lecture
            join fetch lecture.instructor
            where approval.requestType = :type
              and approval.status = :status
            order by approval.createdAt desc
            """)
    List<AdminApproval> findLiveApprovalsWithInstructor(
            @Param("type") ApprovalRequestType type,
            @Param("status") ApprovalStatus status
    );

    @Query("""
            select approval
            from AdminApproval approval
            join fetch approval.lecture lecture
            join fetch lecture.instructor
            where approval.requestType = :type
            order by approval.createdAt desc
            """)
    List<AdminApproval> findLiveApprovalsWithInstructor(@Param("type") ApprovalRequestType type);

    @Query("""
            select distinct approval
            from AdminApproval approval
            join fetch approval.settlement settlement
            join fetch settlement.instructor
            left join fetch settlement.details detail
            left join fetch detail.course
            where approval.requestType = :type
              and approval.status = :status
            order by approval.createdAt desc
            """)
    List<AdminApproval> findSettlementApprovalsWithDetails(
            @Param("type") ApprovalRequestType type,
            @Param("status") ApprovalStatus status
    );

    @Query("""
            select distinct approval
            from AdminApproval approval
            join fetch approval.settlement settlement
            join fetch settlement.instructor
            left join fetch settlement.details detail
            left join fetch detail.course
            where approval.requestType = :type
            order by approval.createdAt desc
            """)
    List<AdminApproval> findSettlementApprovalsWithDetails(@Param("type") ApprovalRequestType type);

    boolean existsByCourseIdAndRequestTypeAndStatus(
            Long courseId,
            ApprovalRequestType requestType,
            ApprovalStatus status
    );

    boolean existsBySettlementIdAndRequestTypeAndStatus(
            Long settlementId,
            ApprovalRequestType requestType,
            ApprovalStatus status
    );

    void deleteByInstructorApplications(InstructorApplications instructorApplications);

    void deleteByLecture(LiveLecture lecture);

    void deleteByCourseIdAndRequestTypeAndStatus(
            Long courseId,
            ApprovalRequestType requestType,
            ApprovalStatus status
    );

    void deleteBySettlementIdAndRequestTypeAndStatus(
            Long settlementId,
            ApprovalRequestType requestType,
            ApprovalStatus status
    );
}
