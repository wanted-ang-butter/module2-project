package com.wanted.naeil.domain.learning.service;

import com.wanted.naeil.domain.course.entity.enums.SectionStatus;
import com.wanted.naeil.domain.course.repository.SectionRepository;
import com.wanted.naeil.domain.learning.dto.response.MyCourseResponse;
import com.wanted.naeil.domain.course.entity.Review;
import com.wanted.naeil.domain.course.repository.ReviewRepository;
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

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class MyCourseService {

    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;
    private final LiveReservationRepository liveReservationRepository;
    private final SectionRepository sectionRepository;

    // 내 강의 목록 조회
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

    // 실시간 강의 예약 목록 조회
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
