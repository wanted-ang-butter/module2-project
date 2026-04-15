package com.wanted.naeil.domain.course.service;

import com.wanted.naeil.domain.course.dto.CreateCourseRequest;
import com.wanted.naeil.domain.course.dto.CreateCourseResponse;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import com.wanted.naeil.global.util.file.LocalFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstCourseService {

    private final CourseRepository courseRepository;
    private final LocalFileService localFileService;
    private final ModelMapper modelMapper;

    // 코스 생서 메서드
    @Transactional
    public CreateCourseResponse createCourse(CreateCourseRequest request) {

        // TODO : 강사 권한 체크 및 예외 처리 로직 추가

        // 제목 관련 검증 로직
        if (request.title().isEmpty()) {
            throw new IllegalArgumentException("코스 제목은 필수입니다. 제목을 입력해주세요,");
        }

        if (courseRepository.findByTitle(request.title()).isPresent()) {
            throw new IllegalArgumentException("동일한 이름의 강의가 존재합니다. 다시 작성해주세요.");
        }

        // 카테고리 검증
        if (request.category() == null) {
            throw new IllegalArgumentException("잘못된 카테고리 입니다.");
        }

        // 코스 설명 검증
        if (request.description().isEmpty()) {
            throw new IllegalArgumentException("코스 설명은 필수입니다. 제목을 입력해주세요,");
        }

        // 가격 검증
        if (request.price() < 0) {
            throw new IllegalArgumentException("최소 0원 이상의 가격을 입력해주세요.");
        }

        // TODO : 썸네일 파일 업로드 기능 구현
        MultipartFile thumbnailImage = request.thumbnail();
        if (thumbnailImage == null || thumbnailImage.isEmpty()) {
            throw new IllegalArgumentException("썸네일 이미지는 필수입니다. 파일을 첨부해주세요.");
        }

        String thumbnailUrl = localFileService.uploadSingleFile(thumbnailImage, "courses");

        Course course = Course.builder()
//                .instructor() TODO : 추후 승재 구현되면 세션에서 뽑아 쓰기
                .category(request.category())
                .title(request.title())
                .description(request.description())
                .price(request.price())
                .thumbnail(thumbnailUrl)
                .build();

        Course savedCourse = courseRepository.save(course);
        log.info("[코스 생성] 코스가 정상적으로 등록 됐습니다. course_id: {}", savedCourse.getId());


        // TODO : createSection() 만들어서 호출하기

        return CreateCourseResponse.from(savedCourse, "강의 등록 신청이 완료되었습니다. 관리자 승인 후 강의가 활성화됩니다.");
    }
}
