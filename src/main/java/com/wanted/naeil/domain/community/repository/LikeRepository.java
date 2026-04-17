package com.wanted.naeil.domain.community.repository;

import com.wanted.naeil.domain.community.entity.Like;
import com.wanted.naeil.domain.community.entity.Post;
import com.wanted.naeil.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndPost(User user, Post post);

    long countByPost(Post post);

    Optional<Like> findByLikeId(Long likeId);
}