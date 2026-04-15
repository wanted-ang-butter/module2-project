package com.wanted.naeil.domain.course.service;

import com.wanted.naeil.domain.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstCourseService {

    private final CourseRepository courseRepository;
}
