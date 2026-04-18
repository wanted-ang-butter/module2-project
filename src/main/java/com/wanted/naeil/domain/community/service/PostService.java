package com.wanted.naeil.domain.community.service;

import com.wanted.naeil.domain.community.dto.request.PostCreateRequest;
import com.wanted.naeil.domain.community.dto.request.PostUpdateRequest;
import com.wanted.naeil.domain.community.dto.response.CommentResponse;
import com.wanted.naeil.domain.community.dto.response.PostDetailResponse;
import com.wanted.naeil.domain.community.dto.response.PostListResponse;
import com.wanted.naeil.domain.community.entity.Like;
import com.wanted.naeil.domain.community.entity.Post;
import com.wanted.naeil.domain.community.entity.enums.PostCategory;
import com.wanted.naeil.domain.community.repository.CommentRepository;
import com.wanted.naeil.domain.community.repository.LikeRepository;
import com.wanted.naeil.domain.community.repository.PostRepository;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import com.wanted.naeil.domain.user.entity.enums.Role;
import com.wanted.naeil.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CourseRepository courseRepository;
    private final CommentRepository commentRepository;

    // 글 목록 조회
    @Transactional(readOnly = true)
    public List<PostListResponse> getPostList(PostCategory category, String sortType) {
        List<Post> posts = switch (sortType) {
            case "oldest"  -> postRepository.findByCategoryAndIsPublicTrueOrderByCreatedAtAsc(category);
            case "popular" -> postRepository.findByCategoryAndIsPublicTrueOrderByViewCountDesc(category);
            default        -> postRepository.findByCategoryAndIsPublicTrueOrderByCreatedAtDesc(category);
        };

        return posts.stream()
                .map(post -> {
                    long likeCount = likeRepository.countByPost(post);
                    return PostListResponse.from(post, likeCount);
                })
                .collect(Collectors.toList());
    }

    // 글 상세 조회
    @Transactional
    public PostDetailResponse getPost(Long postId, User loginUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("비공개 처리 되었거나 삭제된 게시글입니다."));

        post.increaseViewCount();

        boolean isLiked = false;
        Long likeId = null;

        if (loginUser != null) {
            Optional<Like> like = likeRepository.findByUserAndPost(loginUser, post);
            if (like.isPresent()) {
                isLiked = true;
                likeId = like.get().getLikeId();
            }
        }

        long likeCount = likeRepository.countByPost(post);

        List<CommentResponse> comments = commentRepository.findByPostOrderByCreatedAtAsc(post)
                .stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());

        return PostDetailResponse.from(post, likeCount, isLiked, likeId, comments);
    }

    // 글 작성
    @Transactional
    public void createPost(PostCreateRequest request, User loginUser) {
        Course course = null;
        if (request.getCourseId() != null) {
            course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new NoSuchElementException("연결하려는 강의 정보를 찾을 수 없습니다."));
        }

        Post post = Post.builder()
                .user(loginUser)
                .course(course)
                .category(request.getCategory())
                .title(request.getTitle())
                .content(request.getContent())
                .isPublic(request.getIsPublic() == null || request.getIsPublic())
                .build();

        postRepository.save(post);
    }

    // 글 수정
    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, User loginUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글이 존재하지 않습니다."));

        validateOwner(post, loginUser);

        post.update(request.getTitle(), request.getContent(),
                request.getIsPublic() != null ? request.getIsPublic() : post.isPublic());
    }

    // 글 삭제
    @Transactional
    public void deletePost(Long postId, User loginUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글이 존재하지 않습니다."));

        boolean isOwner = post.getUser().getId().equals(loginUser.getId());
        boolean isAdmin = loginUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("해당 게시물에 삭제 권한이 없습니다.");
        }

        postRepository.delete(post); // 엔티티의 @SQLDelete가 작동함
    }

    // Q&A 해결 상태 변경
    @Transactional
    public void toggleResolved(Long postId, User loginUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글이 존재하지 않습니다."));

        validateOwner(post, loginUser);
        post.toggleResolved();
    }

    // 권한 검증 공통 메서드
    private void validateOwner(Post post, User loginUser) {
        if (!post.getUser().getId().equals(loginUser.getId())) {
            throw new AccessDeniedException("해당 기능에 대한 접속 권한이 없습니다.");
        }
    }
}