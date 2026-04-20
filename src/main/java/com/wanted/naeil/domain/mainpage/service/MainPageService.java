package com.wanted.naeil.domain.mainpage.service;

import com.wanted.naeil.domain.course.dto.response.CourseListResponse;
import com.wanted.naeil.domain.course.repository.CategoryRepository;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import com.wanted.naeil.domain.learning.entity.Enrollment;
import com.wanted.naeil.domain.learning.repository.EnrollmentRepository;
import com.wanted.naeil.domain.mainpage.dto.response.MainCategoryResponse;
import com.wanted.naeil.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainPageService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final EnrollmentRepository enrollmentRepository;

    // 인기 강의 조회 - 수강생 많은 순 상위 10개
    // category가 null이면 전체, 값이 있으면 해당 카테고리만 필터링
    public List<CourseListResponse> getPopularCourses(String category) {
        return courseRepository.findAllOrderByStudentCountDesc()
                .stream()
                .filter(course -> category == null || course.getCategory().equals(category))
                .limit(10)
                .collect(Collectors.toList());
    }

    // 신규 강의 조회 - 최신 등록순 상위 10개
    // category가 null이면 전체, 값이 있으면 해당 카테고리만 필터링
    public List<CourseListResponse> getNewCourses(String category) {
        return courseRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .filter(course -> category == null || course.getCategory().equals(category))
                .limit(10)
                .collect(Collectors.toList());
    }

    // 카테고리별 강의 조회 - 카테고리당 최대 6개
    public List<MainCategoryResponse> getCategoryCourses() {
        List<CourseListResponse> allCourses = courseRepository.findAllWithStatus();

        return categoryRepository.findAll()
                .stream()
                .map(category -> {
                    List<CourseListResponse> courses = allCourses.stream()
                            .filter(course -> course.getCategory().equals(category.getName()))
                            .limit(6)
                            .toList();

                    return MainCategoryResponse.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .courses(courses)
                            .build();
                })
                .filter(category -> !category.getCourses().isEmpty())
                .toList();
    }

    // 수강 중인 강의 수 조회
    @Transactional(readOnly = true)
    public long getEnrolledCount(User loginUser) {
        return enrollmentRepository.findByUser(loginUser).size();
    }

    // 평균 진행률 조회
    @Transactional(readOnly = true)
    public double getAverageProgress(User loginUser) {
        List<Enrollment> enrollments = enrollmentRepository.findByUser(loginUser);
        if (enrollments.isEmpty()) return 0;

        return enrollments.stream()
                .mapToDouble(Enrollment::getCoursesRate)
                .average()
                .orElse(0);
    }

    // 추천 강의 조회 - 최대 6개
    @Transactional(readOnly = true)
    public List<CourseListResponse> getRecommendedCourses(User loginUser) {
        List<Enrollment> enrollments = enrollmentRepository.findByUser(loginUser);

        // 수강 중인 강의 없으면 전체 인기순으로 추천
        if (enrollments.isEmpty()) {
            return getPopularCourses(null);
        }

        // 수강 중인 강의가 있으면 같은 카테고리 강의 추천 (이미 수강 중인 강의는 제외)
        Set<String> enrolledCategories = enrollments.stream()
                .map(e -> e.getCourse().getCategory().getName())
                .collect(Collectors.toSet());
        Set<Long> enrolledCourseIds = enrollments.stream()
                .map(e -> e.getCourse().getId())
                .collect(Collectors.toSet());

        return courseRepository.findAllWithStatus()
                .stream()
                .filter(course -> enrolledCategories.contains(course.getCategory()))
                .filter(course -> !enrolledCourseIds.contains(course.getCourseId()))
                .limit(6)
                .toList();
    }

    // 강의 검색
    public List<CourseListResponse> searchCourses(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return courseRepository.searchByKeyword(keyword);
    }
}
