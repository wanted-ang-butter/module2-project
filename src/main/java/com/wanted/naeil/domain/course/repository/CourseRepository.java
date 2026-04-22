package com.wanted.naeil.domain.course.repository;

import com.wanted.naeil.domain.course.dto.response.CourseListResponse;
import com.wanted.naeil.domain.course.entity.Category;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.entity.enums.CourseStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
            "WHERE c.status = :status " +
            "GROUP BY c.id, cat.name, u.name, c.thumbnail, c.title, c.description, c.price")
    List<CourseListResponse> findAllWithStatus(@Param("status") CourseStatus status);

    @Query("select new com.wanted.naeil.domain.course.dto.response.CourseListResponse(" +
            "c.id, c.thumbnail, cat.name, c.title, c.description, u.name, " +
            "AVG(r.rating), COUNT(distinct e.id), c.price) " +
            "FROM Course c " +
            "JOIN c.category cat " +
            "JOIN c.instructor u " +
            "LEFT join Review r ON r.course = c " +
            "LEFT JOIN Enrollment e ON e.course = c " +
            "WHERE c.status = :status " +
            "GROUP BY c.id, cat.name, u.name, c.thumbnail, c.title, c.description, c.price " +
            "ORDER BY c.createdAt DESC")
    List<CourseListResponse> findAllOrderByCreatedAtDesc(@Param("status") CourseStatus status);


    @Query("select new com.wanted.naeil.domain.course.dto.response.CourseListResponse(" +
            "c.id, c.thumbnail, cat.name, c.title, c.description, u.name, " +
            "AVG(r.rating), COUNT(distinct e.id), c.price) " +
            "FROM Course c " +
            "JOIN c.category cat " +
            "JOIN c.instructor u " +
            "LEFT join Review r ON r.course = c " +
            "LEFT JOIN Enrollment e ON e.course = c " +
            "WHERE c.status = :status " +
            "GROUP BY c.id, cat.name, u.name, c.thumbnail, c.title, c.description, c.price " +
            "ORDER BY COUNT(distinct e.id) DESC")
    List<CourseListResponse> findAllOrderByStudentCountDesc(@Param("status") CourseStatus status);


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

    // 특정 카테고리 + 승인된 강의 전체 조회
    @Query("select new com.wanted.naeil.domain.course.dto.response.CourseListResponse(" +
            "c.id, c.thumbnail, cat.name, c.title, c.description, u.name, " +
            "AVG(r.rating), COUNT(distinct e.id), c.price) " +
            "FROM Course c " +
            "JOIN c.category cat " +
            "JOIN c.instructor u " +
            "LEFT join Review r ON r.course = c " +
            "LEFT JOIN Enrollment e ON e.course = c " +
            "WHERE c.status = :status " +
            "AND cat.name = :category " +
            "GROUP BY c.id, cat.name, u.name, c.thumbnail, c.title, c.description, c.price")
    List<CourseListResponse> findAllByCategoryNameAndStatus(
            @Param("category") String category,
            @Param("status") CourseStatus status
    );

    // 강의 검색 기능 : 최신순
    @Query("select new com.wanted.naeil.domain.course.dto.response.CourseListResponse(" +
            "c.id, c.thumbnail, cat.name, c.title, c.description, u.name, " +
            "AVG(r.rating), COUNT(distinct e.id), c.price) " +
            "FROM Course c " +
            "JOIN c.category cat " +
            "JOIN c.instructor u " +
            "LEFT JOIN Review r ON r.course = c " +
            "LEFT JOIN Enrollment e ON e.course = c " +
            "WHERE c.status = :status " +
            "AND (:category IS NULL OR cat.name = :category) " +
            "AND (:keyword IS NULL OR (c.title LIKE %:keyword% OR c.description LIKE %:keyword% OR u.name LIKE %:keyword%)) " +
            "GROUP BY c.id, cat.name, u.name, c.thumbnail, c.title, c.description, c.price, c.createdAt " +
            "ORDER BY c.createdAt DESC")
    Slice<CourseListResponse> searchCourseListLatest(
            @Param("category") String category,
            @Param("keyword") String keyword,
            @Param("status") CourseStatus status,
            Pageable pageable
    );

    // 오래된 순
    @Query("select new com.wanted.naeil.domain.course.dto.response.CourseListResponse(" +
            "c.id, c.thumbnail, cat.name, c.title, c.description, u.name, " +
            "AVG(r.rating), COUNT(distinct e.id), c.price) " +
            "FROM Course c " +
            "JOIN c.category cat " +
            "JOIN c.instructor u " +
            "LEFT JOIN Review r ON r.course = c " +
            "LEFT JOIN Enrollment e ON e.course = c " +
            "WHERE c.status = :status " +
            "AND (:category IS NULL OR cat.name = :category) " +
            "AND (:keyword IS NULL OR (c.title LIKE %:keyword% OR c.description LIKE %:keyword% OR u.name LIKE %:keyword%)) " +
            "GROUP BY c.id, cat.name, u.name, c.thumbnail, c.title, c.description, c.price, c.createdAt " +
            "ORDER BY c.createdAt ASC")
    Slice<CourseListResponse> searchCourseListOldest(
            @Param("category") String category,
            @Param("keyword") String keyword,
            @Param("status") CourseStatus status,
            Pageable pageable
    );


    // 인기 순
    @Query("select new com.wanted.naeil.domain.course.dto.response.CourseListResponse(" +
            "c.id, c.thumbnail, cat.name, c.title, c.description, u.name, " +
            "AVG(r.rating), COUNT(distinct e.id), c.price) " +
            "FROM Course c " +
            "JOIN c.category cat " +
            "JOIN c.instructor u " +
            "LEFT JOIN Review r ON r.course = c " +
            "LEFT JOIN Enrollment e ON e.course = c " +
            "WHERE c.status = :status " +
            "AND (:category IS NULL OR cat.name = :category) " +
            "AND (:keyword IS NULL OR (c.title LIKE %:keyword% OR c.description LIKE %:keyword% OR u.name LIKE %:keyword%)) " +
            "GROUP BY c.id, cat.name, u.name, c.thumbnail, c.title, c.description, c.price, c.createdAt " +
            "ORDER BY COUNT(distinct e.id) DESC, c.createdAt DESC")
    Slice<CourseListResponse> searchCourseListPopular(
            @Param("category") String category,
            @Param("keyword") String keyword,
            @Param("status") CourseStatus status,
            Pageable pageable
    );


}
