package com.wanted.naeil.domain.community.repository;

import com.wanted.naeil.domain.community.entity.Post;
import com.wanted.naeil.domain.community.entity.enums.PostCategory;
import com.wanted.naeil.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 목록 조회 - 최신순 (기본)
    List<Post> findByCategoryAndIsPublicTrueOrderByCreatedAtDesc(PostCategory category);

    // 목록 조회 - 오래된순
    List<Post> findByCategoryAndIsPublicTrueOrderByCreatedAtAsc(PostCategory category);

    // 목록 조회 - 조회순
    List<Post> findByCategoryAndIsPublicTrueOrderByViewCountDesc(PostCategory category);

    // Q&A - 특정 강의 필터 + 최신순
    List<Post> findByCategoryAndCourseAndIsPublicTrueOrderByCreatedAtDesc(PostCategory category, Course course);
}