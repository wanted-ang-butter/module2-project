package com.wanted.naeil.domain.course.repository;

import com.wanted.naeil.domain.course.dto.response.CourseDetailsResponse;
import com.wanted.naeil.domain.course.dto.response.CourseListResponse;
import com.wanted.naeil.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository <Course, Long> {

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


    @Query("SELECT new com.wanted.naeil.domain.course.dto.response.CourseDetailsResponse(" +
            "c.id, cat.name, c.title, u.name, c.thumbnail, c.price, c.description, " +
            "(SELECT COUNT(l) FROM Like l WHERE l.course = c AND l.targetType = 'COURSE'), " +
            "SIZE(c.sections)," +
            "(SELECT COUNT(e) FROM Enrollment e WHERE e.course = c))" +
            "FROM Course c " +
            "JOIN c.category cat " +
            "JOIN c.instructor u " +
            "WHERE c.id = :couserId")
    Optional<CourseDetailsResponse> findCourseDetailsDtoById(@Param("courseId") Long courseId);
}
