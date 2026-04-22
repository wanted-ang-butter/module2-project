package com.wanted.naeil.domain.course.controller;

import com.wanted.naeil.domain.course.dto.response.CourseDetailsResponse;
import com.wanted.naeil.domain.course.dto.response.CourseListResponse;
import com.wanted.naeil.domain.course.repository.CategoryRepository;
import com.wanted.naeil.domain.course.service.CourseService;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/course")
@RequiredArgsConstructor
@Slf4j
public class UserCourseController {

    private final CourseService courseService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ModelAndView showAllCourses(
            @AuthenticationPrincipal AuthDetails authDetails,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            ModelAndView mv
    ) {

        log.info("[Course] 전체 코스 목록 페이지 조회 - category: {}, keyword: {}", category, keyword);

        int pageSize = 12;
        int currentPage = Math.max(page, 0);

        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Slice<CourseListResponse> coursePage = courseService
                .getCourses(category, keyword, sort, pageable);

        if (authDetails != null) {
            mv.addObject("user", authDetails.getLoginUserDTO());
        }

        mv.addObject("courses", coursePage.getContent());
        mv.addObject("currentPage", currentPage);
        mv.addObject("hasPrevious", coursePage.hasPrevious());
        mv.addObject("hasNext", coursePage.hasNext());
        mv.addObject("selectedSort", sort);
        mv.addObject("categories", categoryRepository.findAll());
        mv.addObject("selectedCategory", category);
        mv.addObject("keyword", keyword);
        mv.setViewName("course/courseList");
        return mv;
    }

    @GetMapping("/{course_id}")
    public ModelAndView getCourseDetails(
            @AuthenticationPrincipal AuthDetails authDetails,
            ModelAndView mv,
            @PathVariable("course_id") Long courseId
    ) {

        log.info("[Course] 코스 상세 페이지 조회");

        User loginUser = authDetails != null
                ? userRepository.findByUsername(authDetails.getLoginUserDTO().getUsername())
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."))
                : null;

        CourseDetailsResponse course = courseService.getCourseDetail(courseId, loginUser);

        if (authDetails != null) {
            mv.addObject("user", authDetails.getLoginUserDTO());
        }

        mv.addObject("course", course);
        mv.setViewName("course/courseDetail");
        return mv;
    }
}
