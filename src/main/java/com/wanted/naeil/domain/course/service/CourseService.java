package com.wanted.naeil.domain.course.service;

import com.wanted.naeil.domain.course.dto.request.CreateCourseRequest;
import com.wanted.naeil.domain.course.dto.response.CourseListResponse;
import com.wanted.naeil.domain.course.dto.response.CreateCourseResponse;
import com.wanted.naeil.domain.course.entity.Category;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.repository.CategoryRepository;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import com.wanted.naeil.global.util.file.LocalFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LocalFileService localFileService;
    private final CategoryRepository categoryRepository;
    private final SectionService sectionService;
    private final ModelMapper modelMapper;

    // 코스 생서 메서드
    @Transactional
    public CreateCourseResponse createCourse(CreateCourseRequest request) {

        // TODO : 추후 승재 병합 후, 세션에서 뽑아오기로 수정
        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new IllegalArgumentException("강사 정보를 찾을 수 없습니다. ID: " + request.getInstructorId()));

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

        // TODO : 썸네일 파일 업로드 기능 구현
        MultipartFile thumbnailImage = request.getThumbnail();
        if (thumbnailImage == null || thumbnailImage.isEmpty()) {
            throw new IllegalArgumentException("썸네일 이미지는 필수입니다. 파일을 첨부해주세요.");
        }

        String thumbnailUrl = localFileService.uploadSingleFile(thumbnailImage, "courses");

        Course course = Course.builder()
                // TODO : 추후 승재 구현되면 세션에서 뽑아 쓰기
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
            sectionService.createSection(course, request.getSections());
        }

        return CreateCourseResponse.from(savedCourse, "강의 등록 신청이 완료되었습니다. 관리자 승인 후 강의가 활성화됩니다.");
    }

    @Transactional(readOnly = true)
    public List<CourseListResponse> findAllCourses() {
        return courseRepository.findAllWithStatus();
    }
}
