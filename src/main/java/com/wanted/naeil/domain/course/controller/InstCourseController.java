package com.wanted.naeil.domain.course.controller;

import com.wanted.naeil.domain.course.service.InstCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/instructor")
@RequiredArgsConstructor
public class InstCourseController {

    private final InstCourseService instCourseService;
}
