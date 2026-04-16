package com.wanted.naeil.domain.course.repository;

import com.wanted.naeil.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository <Course, Long> {

    Optional<Course>findByTitle(String title);

    List<Course> findAllByOrderByTitleAsc();
}
