package com.wanted.naeil.domain.course.repository;

import com.wanted.naeil.domain.course.dto.CurriculumSectionDTO;
import com.wanted.naeil.domain.course.dto.SectionStudyMainDTO;
import com.wanted.naeil.domain.course.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {

    // 강의 Id로 섹션 조회
    @Query("SELECT s FROM Section s WHERE s.course.id = :courseId ORDER BY s.sequence ASC")
    List<Section> findByCourseId(@Param("courseId") Long courseId);


    @Query("""
    select new com.wanted.naeil.domain.course.dto.SectionStudyMainDTO(
        c.id, cat.name, c.title, i.name, s.id, s.title, s.videoUrl, s.attachmentUrl, lp.status
        )
        from Section s
        JOIN s.course c
        join c.category cat
        join c.instructor i
        left join LearningProgress lp
            on lp.section = s and lp.user.id = :userId
        where c.id = :courseId and s.id = :sectionId
        and s.status = com.wanted.naeil.domain.course.entity.SectionStatus.ACTIVE
    """)
    Optional<SectionStudyMainDTO> findSectionStudyMain(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId,
            @Param("sectionId") Long sectionId
    );


    @Query("""
    select new com.wanted.naeil.domain.course.dto.CurriculumSectionDTO(
        s.id,
        s.title,
        s.playTime,
        lp.status
    )
    from Section s
    left join LearningProgress lp
        on lp.section = s
       and lp.user.id = :userId
    where s.course.id = :courseId
      and s.status = com.wanted.naeil.domain.course.entity.SectionStatus.ACTIVE
    order by s.sequence asc
""")
    List<CurriculumSectionDTO> findCurriculumSections(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId
    );
}
