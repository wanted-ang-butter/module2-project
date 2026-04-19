package com.wanted.naeil.domain.community.repository;

import com.wanted.naeil.domain.community.entity.Like;
import com.wanted.naeil.domain.community.entity.Post;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // 게시글 좋아요 단건 조회
    Optional<Like> findByUserAndPost(User user, Post post);

    // 강의 좋아요 단건 조회
    Optional<Like> findByUserAndCourse(User user, Course course);

    long countByPost(Post post);
}