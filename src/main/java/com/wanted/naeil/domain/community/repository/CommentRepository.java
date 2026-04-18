package com.wanted.naeil.domain.community.repository;

import com.wanted.naeil.domain.community.entity.Comment;
import com.wanted.naeil.domain.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostOrderByCreatedAtAsc(Post post);
}
