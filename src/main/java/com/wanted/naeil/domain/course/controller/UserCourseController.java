package com.wanted.naeil.domain.course.controller;

import com.wanted.naeil.domain.course.dto.response.CourseDetailsResponse;
import com.wanted.naeil.domain.course.dto.response.CourseListResponse;
import com.wanted.naeil.domain.course.repository.CategoryRepository;
import com.wanted.naeil.domain.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/course")
@RequiredArgsConstructor
@Slf4j
public class UserCourseController {

    private final CourseService courseService;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public ModelAndView showAllCourses(ModelAndView mv) {

        log.info("[Course] 전체 코스 목록 페이지 조회");

        List<CourseListResponse> courses = courseService.getAllCourses();

        mv.addObject("courses", courses);
        mv.addObject("categories", categoryRepository.findAll());
        mv.setViewName("course/courseList");
        return mv;
    }

    @GetMapping("/{course_id}")
    public ModelAndView getCourseDetails(ModelAndView mv, @PathVariable("course_id") Long courseId) {

        log.info("[Course] 코스 상세 페이지 조회");

        CourseDetailsResponse course = courseService.getCourseDetail(courseId);
        mv.addObject("course", course);
        mv.setViewName("course/courseDetail");
        return mv;
    }
}
