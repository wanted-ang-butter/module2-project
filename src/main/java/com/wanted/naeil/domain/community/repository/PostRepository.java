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

    // 게시글 상세 조회
    Optional<Post> findByPostIdAndDeletedAtIsNull(Long postId);

    // 목록 조회 - 최신순 (자유게시판 기본 + Q&A)
    List<Post> findByCategoryAndIsPublicTrueAndDeletedAtIsNullOrderByCreatedAtDesc(PostCategory category);

    // 자유게시판 - 오래된순
    List<Post> findByCategoryAndIsPublicTrueAndDeletedAtIsNullOrderByCreatedAtAsc(PostCategory category);

    // 자유게시판 - 조회순 (조회수 기준)
    List<Post> findByCategoryAndIsPublicTrueAndDeletedAtIsNullOrderByViewCountDesc(PostCategory category);

    // Q&A - 특정 강의 필터 + 최신순
    List<Post> findByCategoryAndCourseAndIsPublicTrueAndDeletedAtIsNullOrderByCreatedAtDesc(PostCategory category, Course course);
}
