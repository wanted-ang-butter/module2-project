package com.wanted.naeil.domain.course.repository;


import com.wanted.naeil.domain.course.dto.response.CourseListResponse;
import com.wanted.naeil.domain.course.entity.Category;
import com.wanted.naeil.domain.course.entity.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository <Course, Long> {
    boolean existsByCategoryId(Long categoryId);
    Optional<Course>findByTitle(String title); //성민 추가
    long countByCategory(Category category);

    // 코스 전체 조회
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

    // 내가 등록한 강의 - 강사
    @EntityGraph(attributePaths = {"category"})
    List<Course> findByInstructorIdOrderByCreatedAtDesc(Long instructorId);

    // 코스 상세 조회
    @EntityGraph(attributePaths = {"category", "instructor"})
    Optional<Course> findCourseDetailsById(@Param("courseId") Long courseId);

    // 코스 좋아요 수
    @Query("SELECT COUNT(l) FROM Like l where l.course.id = :courseId")
    long countLikesByCourseId(@Param("courseId") Long courseId);

    // 코스 수강생 수
    @Query("select count(e) from Enrollment e where e.course.id = :courseId")
    long countStudentsByCourseId(@Param("courseId") Long courseId);

    // 코스 별점 평균
    @Query("select avg(r.rating) from Review r where r.course.id = :courseId")
    Double getAverageRatingByCourseId(@Param("courseId") Long courseId);

}
