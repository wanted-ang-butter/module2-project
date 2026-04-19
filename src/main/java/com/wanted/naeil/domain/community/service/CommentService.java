package com.wanted.naeil.domain.community.service;

import com.wanted.naeil.domain.community.dto.request.CommentCreateRequest;
import com.wanted.naeil.domain.community.dto.request.CommentUpdateRequest;
import com.wanted.naeil.domain.community.entity.Comment;
import com.wanted.naeil.domain.community.entity.Post;
import com.wanted.naeil.domain.community.repository.CommentRepository;
import com.wanted.naeil.domain.community.repository.PostRepository;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.entity.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 댓글 작성
    @Transactional
    public void createComment(Long postId, CommentCreateRequest request, User loginUser) {

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("내용을 입력해주세요.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글이 존재하지 않습니다."));

        Comment comment = Comment.builder()
                .post(post)
                .user(loginUser)
                .content(request.getContent())
                .build();

        commentRepository.save(comment);
    }

    // 댓글 수정
    @Transactional
    public void updateComment(Long commentId, CommentUpdateRequest request, User loginUser) {

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("내용을 입력해주세요.");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("해당 댓글이 존재하지 않습니다."));

        validateOwner(comment, loginUser);

        comment.update(request.getContent());
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, User loginUser) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("해당 댓글이 존재하지 않습니다."));

        boolean isOwner = comment.getUser().getId().equals(loginUser.getId());
        boolean isAdmin = loginUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("해당 기능에 대한 접속 권한이 없습니다.");
        }
        commentRepository.delete(comment);
    }

    // 권한 검증 공통 메서드
    private void validateOwner(Comment comment, User loginUser) {
        if (!comment.getUser().getId().equals(loginUser.getId())) {
            throw new AccessDeniedException("해당 기능에 대한 접속 권한이 없습니다.");
        }
    }
}
