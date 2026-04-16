package com.wanted.naeil.domain.course.controller;

import com.wanted.naeil.domain.course.repository.CategoryRepository;
import com.wanted.naeil.domain.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/course")
@RequiredArgsConstructor
@Slf4j
public class UserCourseController {

    private final CourseService courseService;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public ModelAndView showAllCourses(ModelAndView mv) {
        return mv;
    }
}
