package com.wanted.naeil.domain.course.service;

import com.wanted.naeil.domain.admin.entity.AdminApproval;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalRequestType;
import com.wanted.naeil.domain.admin.entity.enums.ApprovalStatus;
import com.wanted.naeil.domain.admin.repository.AdminApprovalRepository;
import com.wanted.naeil.domain.community.entity.Like;
import com.wanted.naeil.domain.community.repository.LikeRepository;
import com.wanted.naeil.domain.course.dto.request.CourseCreateRequest;
import com.wanted.naeil.domain.course.dto.request.CourseStatusUpdateRequest;
import com.wanted.naeil.domain.course.dto.request.CourseUpdateRequest;
import com.wanted.naeil.domain.course.dto.response.*;
import com.wanted.naeil.domain.course.entity.Category;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.entity.enums.CourseStatus;
import com.wanted.naeil.domain.course.repository.CategoryRepository;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.entity.enums.Role;
import com.wanted.naeil.domain.user.repository.UserRepository;
import com.wanted.naeil.global.util.file.FileTransactionService;
import com.wanted.naeil.global.util.file.LocalFileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import com.wanted.naeil.domain.learning.repository.EnrollmentRepository;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final AdminApprovalRepository adminApprovalRepository;

    private final LocalFileService localFileService;
    private final FileTransactionService fileTransactionService;
    private final CategoryRepository categoryRepository;
    private final SectionService sectionService;
    private final ModelMapper modelMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final LikeRepository likeRepository;


    // 코스 등록 요청 - 강사
    @Transactional
    public CreateCourseResponse createCourse(Long instructorId, CourseCreateRequest request) throws AccessDeniedException {

        // 사용자 존재 확인
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("강사 정보를 찾을 수 없습니다. ID: " + instructorId));

        // 관리자 role값 체크
        if (instructor.getRole() != Role.INSTRUCTOR && instructor.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("강사 권한이 있는 사용자만 강의를 등록할 수 있습니다.");
        }

        // 제목 관련 검증 로직
        if (request.getTitle().isEmpty()) {
            throw new IllegalArgumentException("코스 제목은 필수입니다. 제목을 입력해주세요,");
        }

        if (courseRepository.findByTitle(request.getTitle()).isPresent()) {
            throw new IllegalArgumentException("동일한 이름의 강의가 존재합니다. 다시 작성해주세요.");
        }

        // TODO : 추후 categoryRepo 에서 findById로 존재 여부 확인
        // 카테고리 검증
        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("잘못된 카테고리 입니다.");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID입니다."));

        // 코스 설명 검증
        if (request.getDescription().isEmpty()) {
            throw new IllegalArgumentException("코스 설명은 필수입니다. 제목을 입력해주세요,");
        }

        // 가격 검증
        if (request.getPrice() < 0) {
            throw new IllegalArgumentException("최소 0원 이상의 가격을 입력해주세요.");
        }

        // 섬네일 이미지 업로드
        MultipartFile thumbnailImage = request.getThumbnail();
        if (thumbnailImage == null || thumbnailImage.isEmpty()) {
            throw new IllegalArgumentException("썸네일 이미지는 필수입니다. 파일을 첨부해주세요.");
        }

        String thumbnailUrl = localFileService.uploadSingleFile(thumbnailImage, "courses");

        Course course = Course.builder()
                .instructor(instructor)
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .thumbnail(thumbnailUrl)
                .build();

        Course savedCourse = courseRepository.save(course);

        log.info("[코스 생성] 코스가 정상적으로 등록 됐습니다. course_id: {}", savedCourse.getId());


        // 강사 승인 테이블에 요청 로직
        AdminApproval adminApproval = AdminApproval.builder()
                .course(savedCourse)
                .requestType(ApprovalRequestType.COURSE_REGISTER)
                .build();

        adminApprovalRepository.save(adminApproval);

        if (request.getSections() != null && !request.getSections().isEmpty()) {
            sectionService.registerSections(course, request.getSections());
        }

        return CreateCourseResponse.from(savedCourse, "강의 등록 신청이 완료되었습니다. 관리자 승인 후 강의가 활성화됩니다.");
    }

    // 내가 등록한 강의 조회 - 강사
    @Transactional(readOnly = true)
    public List<InstructorCourseResponse> getInstructorCourses(Long instructorId) {

        // 사용자 존재 확인
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("강사 정보를 찾을 수 없습니다. ID: " + instructorId));

        // 관리자 role값 체크
        if (instructor.getRole() != Role.INSTRUCTOR && instructor.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("강사만 접속이 가능합니다.");
        }

        return courseRepository.findByInstructorIdOrderByCreatedAtDesc(instructorId).stream()
                .map(course -> {
                    long studentCount = courseRepository.countStudentsByCourseId(course.getId());
                    Double avgRating = courseRepository.getAverageRatingByCourseId(course.getId());

                    return InstructorCourseResponse.of(course, studentCount, avgRating);
                }).toList();
    }

    // 코스 수정 페이지 조회 - 강사
    @Transactional(readOnly = true)
    public CourseEditResponse getCourseEdit(Long instructorId, Long courseId) {

        // 회원 검증
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강사 정보를 찾을 수 없습니다. ID: " + instructorId));

        // 본인 코스 검증
        validateCourseOwner(course, instructorId);
        log.info("[courseEdit] 본인 인증 성공 & 수정 페이지 진입, 강사 번호 : {}", instructorId);

        List<CourseEditSectionResponse> sections = sectionService.getSectionEdit(courseId);
        log.info("[courseEdit] 섹션 조회 성공!");

        return CourseEditResponse.of(course, sections);
    }

    // 코스 전체 조회
    @Transactional(readOnly = true)
    public List<CourseListResponse> getAllCourses(String category) {
        if (category == null || category.isBlank()) {
            return courseRepository.findAllWithStatus(CourseStatus.ACTIVE);
        }

        return courseRepository.findAllByCategoryNameAndStatus(category, CourseStatus.ACTIVE);
    }


    // 코스 단일 조회 - 공통
    @Transactional(readOnly = true)
    public CourseDetailsResponse getCourseDetail(Long courseId, User loginUser) {
        
        // 강의,카테고리,강사,섹션 한 번에 조회
        Course course = courseRepository.findCourseDetailsById(courseId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 코스입니다. 관리자에게 문의해주세요."));

        long likeCount = courseRepository.countLikesByCourseId(courseId);
        long studentCount = courseRepository.countStudentsByCourseId(courseId);
        Double avgRating = courseRepository.getAverageRatingByCourseId(courseId);

        // 섹션 리스트 조회
        List<SectionListResponse> sectionsResponses = sectionService.getSectionsByCourseId(courseId);

        boolean enrolled = false;

        if (loginUser != null) {
            enrolled = enrollmentRepository.existsByUserIdAndCourseId(loginUser.getId(), courseId);
        }
        // 좋아요 여부 확인
        boolean isLiked = false;
        Long likeId = null;
        if (loginUser != null) {
            Optional<Like> like = likeRepository.findByUserAndCourse(loginUser, course);
            if (like.isPresent()) {
                isLiked = true;
                likeId = like.get().getLikeId();
            }
        }
        return CourseDetailsResponse.of(
                course,
                studentCount,
                likeCount,
                avgRating,
                sectionsResponses,
                isLiked,
                likeId,
                enrolled);
    }

    // 코스 수정
    @Transactional
    public void updateCourse(Long instructorId, Long courseId, @Valid CourseUpdateRequest request) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 강의입니다."));

        validateCourseOwner(course, instructorId);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 앟는 카테고리입니다."));

        String oldThumbnailUrl = course.getThumbnail();
        String newThumbnailUrl = null;
        String thumbnailUrl = oldThumbnailUrl;

        // 새로 받은 썸네일 업데이트
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
            newThumbnailUrl = localFileService.uploadSingleFile(request.getThumbnail(), "courses");
            thumbnailUrl = newThumbnailUrl;
        }

        course.updateBasicInfo(
                request.getTitle(),
                category,
                request.getDescription(),
                request.getPrice(),
                thumbnailUrl
        );

        // 파일 수정은 별도 트랜잭션 관리
        if (newThumbnailUrl != null) {
            fileTransactionService.registerReplace(oldThumbnailUrl, newThumbnailUrl);
        }

        log.info("[CourseUpdate] 강의 기본 정보 수정 완료 - instructorId: {}, courseId: {}", instructorId, courseId);
    }

    // 코스 상태 수정 : 활성 <-> 비활성
    @Transactional
    public void updateCourseStatus(Long instructorId, Long courseId, @Valid CourseStatusUpdateRequest request) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 강의입니다."));

        validateCourseOwner(course, instructorId);

        validateChangeableCourseStatus(course);

        CourseStatus status = request.getStatus();

        if (status == CourseStatus.ACTIVE) {
            course.activate();
        } else if (status == CourseStatus.INACTIVE) {
            course.deactivate();
        } else {
            throw new IllegalArgumentException("변경할 수 없는 강의 상태입니다.");
        }

        log.info("[CourseStatusUpdate] 강의 상태 변경 완료 - instructorId: {}, courseId: {}, status: {}",
                instructorId, courseId, status);
    }

    // 코스 등록 요청 상태 변경 : 승인 대기 <-> 등록 취소
    @Transactional
    public void updateCourseRegistrationStatus(Long instructorId, Long courseId, CourseStatus nextStatus) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 강의입니다."));

        validateCourseOwner(course, instructorId);

        CourseStatus currentStatus = course.getStatus();

        if (currentStatus == CourseStatus.PENDING && nextStatus == CourseStatus.CANCELLED) {

            log.info("[코스 상태 변경] 승인 대기 -> 취소됨 상태 변경 시작!");

            // 승인 대기 -> 취소 했으니까, 관리자 승인 테이블 속 기록 삭제
            adminApprovalRepository.deleteByCourseIdAndRequestTypeAndStatus(
                    courseId,
                    ApprovalRequestType.COURSE_REGISTER,
                    ApprovalStatus.PENDING
            );

            course.cancelRegistration();

            log.info("[코스 상태 변경] 강의 등록 요청 취소 완료 - instructorId: {}, courseId: {}",
                    instructorId, courseId);

            return;
        }

        if (currentStatus == CourseStatus.CANCELLED || currentStatus == CourseStatus.REJECTED
                && nextStatus == CourseStatus.PENDING) {

            log.info("[코스 상태 변경] 취소 / 반려 -> 승인대기 상태 변경 시작!");

            // 승인 대기 테이블에 이미 존재하는지 검증
            boolean alreadyRequested = adminApprovalRepository.existsByCourseIdAndRequestTypeAndStatus(
                    courseId,
                    ApprovalRequestType.COURSE_REGISTER,
                    ApprovalStatus.PENDING
            );

            if (alreadyRequested) {
                throw new IllegalStateException("이미 등록 승인 요청이 진행 중인 강의입니다.");
            }

            // 승인 요청 테이블에 새롭게 추가
            AdminApproval approval = AdminApproval.builder()
                    .course(course)
                    .requestType(ApprovalRequestType.COURSE_REGISTER)
                    .build();

            adminApprovalRepository.save(approval);

            // 코스 승인 대기 상태로 변경
            course.requestRegistration();

            log.info("[코스 상태 변경] 강의 등록 재요청 완료 - instructorId: {}, courseId: {}",
                    instructorId, courseId);
            return;
        }

        log.error("[코스 상태 변경] 🚨 코스 상태 변경 실패 ㅜ 에러가 발생했다..");
        throw new IllegalStateException("현재 상태에서는 요청한 등록 상태로 변경할 수 없습니다.");
    }

    // 코스 삭제 요청
    @Transactional
    public void requestCourseDelete(Long instructorId, Long courseId) {

        log.info("[CourseDeleteRequest] 강의 삭제 요청 시작 - instructorId: {}, courseId: {}",
                instructorId, courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 강의입니다."));

        validateCourseOwner(course, instructorId);

        validateInactiveCourse(course);

        boolean alreadyRequested = adminApprovalRepository.existsByCourseIdAndRequestTypeAndStatus(
                courseId,
                ApprovalRequestType.COURSE_DELETE,
                ApprovalStatus.PENDING // 관리자 승인 테이블에 저장될 status
        );

        if (alreadyRequested) {
            throw new IllegalStateException("이미 삭제 요청이 진행 중인 강의입니다.");
        }

        AdminApproval approval = AdminApproval.builder()
                .course(course)
                .requestType(ApprovalRequestType.COURSE_DELETE)
                .build();

        adminApprovalRepository.save(approval);

        log.info("[CourseDeleteRequest] 강의 삭제 요청 완료 - instructorId: {}, courseId: {}",
                instructorId, courseId);
    }
    // 강사용 강의 상세 조회
    @Transactional(readOnly = true)
    public CourseDetailsResponse getInstructorCourseDetail(Long instructorId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 강의입니다."));
        validateCourseOwner(course, instructorId);
        return getCourseDetail(courseId, null);
    }
    // 강사 강의 상세 페이지 - 해당 강의를 수강 중인 수강생 목록 조회 성민수정
    @Transactional(readOnly = true)
    public List<InstructorCourseStudentResponse> getInstructorCourseStudents(Long instructorId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 강의입니다."));

        validateCourseOwner(course, instructorId);

        return enrollmentRepository.findAllWithUserByCourseIdOrderByCreatedAtDesc(courseId)
                .stream()
                .map(InstructorCourseStudentResponse::from)
                .toList();
    }

    // 검색기능
    @Transactional(readOnly = true)
    public List<CourseListResponse> getCourses(String category, String keyword) {
        String normalizedCategory = (category == null || category.isBlank()) ? null : category;
        String normalizedKeyword = (keyword == null || keyword.isBlank()) ? null : keyword;

        return courseRepository.searchCourseList(
                normalizedCategory,
                normalizedKeyword,
                CourseStatus.ACTIVE
        );
    }


    // ==== 내부 편의 메서드 ====
    private void validateCourseOwner(Course course, Long instructorId) {
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new AccessDeniedException("본인이 생성한 강의만 수정할 수 있습니다.");
        }
    }

    private void validateChangeableCourseStatus(Course course) {
        if (course.getStatus() != CourseStatus.ACTIVE && course.getStatus() != CourseStatus.INACTIVE) {
            throw new IllegalStateException(course.getStatus().getDescription() +
                    " 상태의 강의는 상태를 변경할 수 없습니다.");
        }
    }

    private void validateInactiveCourse(Course course) {
        if (course.getStatus() != CourseStatus.INACTIVE) {
            throw new IllegalStateException("비활성화 상태의 강의만 삭제 요청할 수 있습니다.");
        }
    }
}
