package com.wanted.naeil.domain.course.service;

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

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LocalFileService localFileService;
    private final FileTransactionService fileTransactionService;
    private final CategoryRepository categoryRepository;
    private final SectionService sectionService;
    private final ModelMapper modelMapper;

    // 코스 생성 - 강사
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


        // TODO : createSection() 만들어서 호출하기
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

    // 강의 전체 조회 - 공통
    @Transactional(readOnly = true)
    public List<CourseListResponse> findAllCourses() {
        return courseRepository.findAllWithStatus();
    }

    // 코스 단일 조회 - 공통
    @Transactional(readOnly = true)
    public CourseDetailsResponse getCourseDetail(Long courseId) {
        
        // 강의,카테고리,강사,섹션 한 번에 조회
        Course course = courseRepository.findCourseDetailsById(courseId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 코스입니다. 관리자에게 문의해주세요."));

        long likeCount = courseRepository.countLikesByCourseId(courseId);
        long studentCount = courseRepository.countStudentsByCourseId(courseId);
        Double avgRating = courseRepository.getAverageRatingByCourseId(courseId);

        // 섹션 리스트 조회
        List<SectionListResponse> sectionsResponses = sectionService.getSectionsByCourseId(courseId);

        return CourseDetailsResponse.of(
                course,
                studentCount,
                likeCount,
                avgRating,
                sectionsResponses);
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

    // 코스 상태 수정
    @Transactional
    public void updateCourseStatus(Long instructorId, Long courseId, @Valid CourseStatusUpdateRequest request) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 강의입니다."));

        validateCourseOwner(course, instructorId);

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

    // ==== 내부 편의 메서드 ====
    private void validateCourseOwner(Course course, Long instructorId) {
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new AccessDeniedException("본인이 생성한 강의만 수정할 수 있습니다.");
        }
    }
}
