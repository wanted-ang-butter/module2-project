package com.wanted.naeil.domain.course.repository;

import com.wanted.naeil.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}