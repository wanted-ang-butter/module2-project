package com.wanted.naeil.domain.community.repository;

import com.wanted.naeil.domain.community.entity.Post;
import com.wanted.naeil.domain.community.entity.enums.PostCategory;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 1. 공개글 조회 (일반 유저용)
    List<Post> findByCategoryAndIsPublicTrueOrderByCreatedAtDesc(PostCategory category);
    List<Post> findByCategoryAndIsPublicTrueOrderByCreatedAtAsc(PostCategory category);
    List<Post> findByCategoryAndIsPublicTrueOrderByViewCountDesc(PostCategory category);

    // 2. 특정 유저의 글 조회 (작성자 본인용) - 비공개 포함
    List<Post> findByCategoryAndUserOrderByCreatedAtDesc(PostCategory category, User user);
    List<Post> findByCategoryAndUserOrderByCreatedAtAsc(PostCategory category, User user);
    List<Post> findByCategoryAndUserOrderByViewCountDesc(PostCategory category, User user);

    // 3. 카테고리 전체 조회 (관리자용) - 비공개 포함
    List<Post> findByCategoryOrderByCreatedAtDesc(PostCategory category);
    List<Post> findByCategoryOrderByCreatedAtAsc(PostCategory category);
    List<Post> findByCategoryOrderByViewCountDesc(PostCategory category);

    // 4. Q&A - 특정 강의 필터 + 최신순
    List<Post> findByCategoryAndCourseAndIsPublicTrueOrderByCreatedAtDesc(PostCategory category, Course course);
}