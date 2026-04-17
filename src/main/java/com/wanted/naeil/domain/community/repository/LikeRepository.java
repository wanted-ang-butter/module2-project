package com.wanted.naeil.domain.community.repository;

import com.wanted.naeil.domain.community.entity.Like;
import com.wanted.naeil.domain.community.entity.Post;
import com.wanted.naeil.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // 좋아요 존재 여부 확인
    Optional<Like> findByUserAndPostAndDeletedAtIsNull(User user, Post post);

    // post 좋아요 수 카운트
    long countByPostAndDeletedAtIsNull(Post post);

    // likeId 단건 조회 -> 좋아요 취소 시 사용
    Optional<Like> findByLikeIdAndDeletedAtIsNull(Long likeId);
}
