package com.wanted.naeil.domain.learning.service;

<<<<<<< feature-ajs
import com.wanted.naeil.domain.course.entity.enums.SectionStatus;
import com.wanted.naeil.domain.course.repository.SectionRepository;
import com.wanted.naeil.domain.learning.dto.response.MyCourseResponse;
=======
>>>>>>> develop
import com.wanted.naeil.domain.course.entity.Review;
import com.wanted.naeil.domain.course.repository.ReviewRepository;
import com.wanted.naeil.domain.learning.dto.response.MyCourseDetailResponse;
import com.wanted.naeil.domain.learning.dto.response.MyCourseResponse;
import com.wanted.naeil.domain.learning.dto.response.MySessionResponse;
import com.wanted.naeil.domain.learning.entity.Enrollment;
import com.wanted.naeil.domain.learning.repository.EnrollmentRepository;
import com.wanted.naeil.domain.live.dto.response.MyLiveReservationResponse;
import com.wanted.naeil.domain.live.entity.LiveReservation;
import com.wanted.naeil.domain.live.entity.enums.LiveReservationStatus;
import com.wanted.naeil.domain.live.repository.LiveReservationRepository;
import com.wanted.naeil.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyCourseService {

    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;
    private final LiveReservationRepository liveReservationRepository;
    private final SectionRepository sectionRepository;

    @Transactional(readOnly = true)
    public List<MyCourseResponse> getMyCourses(User loginUser) {

        List<Enrollment> enrollments = enrollmentRepository.findByUserWithCourse(loginUser);

        return enrollments.stream()
                .map(enrollment -> {
                    Optional<Review> review = reviewRepository.findByUserAndCourse(
                            loginUser,
                            enrollment.getCourse()
                    );
                    Long firstSectionId = sectionRepository
                            .findFirstByCourseIdAndStatusOrderBySequenceAsc(
                                    enrollment.getCourse().getId(),
                                    SectionStatus.ACTIVE
                            )
                            .map(section -> section.getId())
                            .orElse(null);

                    return MyCourseResponse.builder()
                            .courseId(enrollment.getCourse().getId())
                            .thumbnail(enrollment.getCourse().getThumbnail())
                            .title(enrollment.getCourse().getTitle())
                            .instructorName(enrollment.getCourse().getInstructor().getName())
                            .coursesRate(enrollment.getCoursesRate())
                            .enrollmentStatus(enrollment.getStatus())
                            .firstSectionId(firstSectionId)
                            .reviewId(review.map(Review::getId).orElse(null))
                            .rating(review.map(Review::getRating).orElse(null))
                            .reviewContent(review.map(Review::getContent).orElse(null))
                            .build();
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public MyCourseDetailResponse getMyCourseDetail(User loginUser, Long courseId) {

        Enrollment enrollment = enrollmentRepository.findByUserAndCourseIdWithDetails(loginUser, courseId)
                .orElseThrow(() -> new IllegalArgumentException("수강 중인 강의만 조회할 수 있습니다."));

        List<MySessionResponse> sessions = Collections.emptyList();

        return MyCourseDetailResponse.builder()
                .courseId(enrollment.getCourse().getId())
                .thumbnail(enrollment.getCourse().getThumbnail())
                .title(enrollment.getCourse().getTitle())
                .instructorName(enrollment.getCourse().getInstructor().getName())
                .description(enrollment.getCourse().getDescription())
                .coursesRate(enrollment.getCoursesRate())
                .completedCount(0)
                .totalCount(0)
                .sessions(sessions)
                .build();
    }

    @Transactional(readOnly = true)
    public List<MyLiveReservationResponse> getLiveReservations(User loginUser) {

        List<LiveReservation> reservations = liveReservationRepository
                .findByUserAndStatusWithLecture(loginUser, LiveReservationStatus.RESERVED);

        return reservations.stream()
                .map(r -> MyLiveReservationResponse.builder()
                        .reservationId(r.getId())
                        .liveId(r.getLiveLecture().getId())
                        .title(r.getLiveLecture().getTitle())
                        .instructorName(r.getLiveLecture().getInstructor().getName())
                        .startAt(r.getLiveLecture().getStartAt())
                        .endAt(r.getLiveLecture().getEndAt())
                        .currentCount(r.getLiveLecture().getCurrentCount())
                        .maxCapacity(r.getLiveLecture().getMaxCapacity())
                        .build())
                .toList();
    }
}