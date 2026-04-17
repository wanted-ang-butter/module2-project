package com.wanted.naeil.domain.community.repository;

import com.wanted.naeil.domain.community.entity.Post;
import com.wanted.naeil.domain.community.entity.PostCategory;
import com.wanted.naeil.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 1. 게시글 상세 조회
    // 기본 findById(postId)를 써도 되지만, 서비스에서 findByPostId를 쓴다면 아래처럼 정의
    Optional<Post> findByPostId(Long postId);

    // 2. 목록 조회 - 최신순 (기본)
    List<Post> findByCategoryAndIsPublicTrueOrderByCreatedAtDesc(PostCategory category);

    // 3. 목록 조회 - 오래된순
    List<Post> findByCategoryAndIsPublicTrueOrderByCreatedAtAsc(PostCategory category);

    // 4. 목록 조회 - 조회순
    List<Post> findByCategoryAndIsPublicTrueOrderByViewCountDesc(PostCategory category);

    // 5. Q&A - 특정 강의 필터 + 최신순
    List<Post> findByCategoryAndCourseAndIsPublicTrueOrderByCreatedAtDesc(PostCategory category, Course course);
}