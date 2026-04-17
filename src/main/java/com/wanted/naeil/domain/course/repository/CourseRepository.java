package com.wanted.naeil.domain.course.repository;

import com.wanted.naeil.domain.course.dto.response.CourseDetailsResponse;
import com.wanted.naeil.domain.course.dto.response.CourseListResponse;
import com.wanted.naeil.domain.course.entity.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository <Course, Long> {
    boolean existsByCategoryId(Long categoryId);
    Optional<Course>findByTitle(String title);

    //group by에는 select 문에 있는 일반 컬럼들은 모두 들어가야함
    @Query("select new com.wanted.naeil.domain.course.dto.response.CourseListResponse(" +
            "c.id, c.thumbnail, cat.name, c.title, c.description, u.name, " +
            "AVG(r.rating), COUNT(distinct e.id), c.price) " +
            "FROM Course c " +
            "JOIN c.category cat " +
            "JOIN c.instructor u " +
            "LEFT join Review r ON r.course = c " +
            "LEFT JOIN Enrollment e ON e.course = c " +
            "GROUP BY c.id, cat.name, u.name, c.thumbnail, c.title, c.description, c.price"
    )
    List<CourseListResponse> findAllWithStatus();


    @EntityGraph(attributePaths = {"category", "instructor", "sections"})
    Optional<Course> findCourseDetailsById(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(l) FROM Like l where l.course.id = :courseId")
    long countLikesByCourseId(@Param("courseId") Long courseId);

    @Query("select count(e) from Enrollment e where e.course.id = :courseId")
    long countStudentsByCourseId(@Param("courseId") Long courseId);

    @Query("select avg(r.rating) from Review r where r.course.id = :courseId")
    Double getAverageRatingByCourseId(@Param("courseId") Long courseId);
}
