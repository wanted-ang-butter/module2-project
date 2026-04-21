package com.wanted.naeil.domain.course.repository;

import com.wanted.naeil.domain.course.dto.response.CourseListResponse;
import com.wanted.naeil.domain.course.entity.Category;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.entity.enums.CourseStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByCategoryId(Long categoryId);

    Optional<Course> findByTitle(String title);

    long countByCategory(Category category);

    long countByStatus(CourseStatus status);

    @Query("select new com.wanted.naeil.domain.course.dto.response.CourseListResponse(" +
            "c.id, c.thumbnail, cat.name, c.title, c.description, u.name, " +
            "AVG(r.rating), COUNT(distinct e.id), c.price) " +
            "FROM Course c " +
            "JOIN c.category cat " +
            "JOIN c.instructor u " +
            "LEFT join Review r ON r.course = c " +
            "LEFT JOIN Enrollment e ON e.course = c " +
            "GROUP BY c.id, cat.name, u.name, c.thumbnail, c.title, c.description, c.price")
    List<CourseListResponse> findAllWithStatus();

    @Query("select new com.wanted.naeil.domain.course.dto.response.CourseListResponse(" +
            "c.id, c.thumbnail, cat.name, c.title, c.description, u.name, " +
            "AVG(r.rating), COUNT(distinct e.id), c.price) " +
            "FROM Course c " +
            "JOIN c.category cat " +
            "JOIN c.instructor u " +
            "LEFT join Review r ON r.course = c " +
            "LEFT JOIN Enrollment e ON e.course = c " +
            "GROUP BY c.id, cat.name, u.name, c.thumbnail, c.title, c.description, c.price " +
            "ORDER BY c.createdAt DESC")
    List<CourseListResponse> findAllOrderByCreatedAtDesc();

    @Query("select new com.wanted.naeil.domain.course.dto.response.CourseListResponse(" +
            "c.id, c.thumbnail, cat.name, c.title, c.description, u.name, " +
            "AVG(r.rating), COUNT(distinct e.id), c.price) " +
            "FROM Course c " +
            "JOIN c.category cat " +
            "JOIN c.instructor u " +
            "LEFT join Review r ON r.course = c " +
            "LEFT JOIN Enrollment e ON e.course = c " +
            "GROUP BY c.id, cat.name, u.name, c.thumbnail, c.title, c.description, c.price " +
            "ORDER BY COUNT(distinct e.id) DESC")
    List<CourseListResponse> findAllOrderByStudentCountDesc();

    @Query("select new com.wanted.naeil.domain.course.dto.response.CourseListResponse(" +
            "c.id, c.thumbnail, cat.name, c.title, c.description, u.name, " +
            "AVG(r.rating), COUNT(distinct e.id), c.price) " +
            "FROM Course c " +
            "JOIN c.category cat " +
            "JOIN c.instructor u " +
            "LEFT join Review r ON r.course = c " +
            "LEFT JOIN Enrollment e ON e.course = c " +
            "WHERE c.title LIKE %:keyword% OR c.description LIKE %:keyword% " +
            "GROUP BY c.id, cat.name, u.name, c.thumbnail, c.title, c.description, c.price")
    List<CourseListResponse> searchByKeyword(@Param("keyword") String keyword);

    @EntityGraph(attributePaths = {"category"})
    List<Course> findByInstructorIdOrderByCreatedAtDesc(Long instructorId);

    @EntityGraph(attributePaths = {"category", "instructor"})
    List<Course> findTop2ByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"category", "instructor"})
    Optional<Course> findCourseDetailsById(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(l) FROM Like l where l.course.id = :courseId")
    long countLikesByCourseId(@Param("courseId") Long courseId);

    @Query("select count(e) from Enrollment e where e.course.id = :courseId")
    long countStudentsByCourseId(@Param("courseId") Long courseId);

    @Query("select avg(r.rating) from Review r where r.course.id = :courseId")
    Double getAverageRatingByCourseId(@Param("courseId") Long courseId);
}
