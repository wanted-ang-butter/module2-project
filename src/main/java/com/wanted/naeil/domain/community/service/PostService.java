package com.wanted.naeil.domain.community.service;

import com.wanted.naeil.domain.community.dto.request.PostCreateRequest;
import com.wanted.naeil.domain.community.dto.request.PostUpdateRequest;
import com.wanted.naeil.domain.community.dto.response.CommentResponse;
import com.wanted.naeil.domain.community.dto.response.PostDetailResponse;
import com.wanted.naeil.domain.community.dto.response.PostListResponse;
import com.wanted.naeil.domain.community.entity.Comment;
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
import com.wanted.naeil.global.common.exception.CustomException;
import com.wanted.naeil.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final CourseRepository courseRepository;

    // 글 목록 조회
    @Transactional(readOnly = true)
    public List<PostListResponse> getPostList(PostCategory category, String sortType) {

        List<Post> posts = switch (sortType) {
            case "oldest"  -> postRepository
                    .findByCategoryAndIsPublicTrueAndDeletedAtIsNullOrderByCreatedAtAsc(category);
            case "popular" -> postRepository
                    .findByCategoryAndIsPublicTrueAndDeletedAtIsNullOrderByViewCountDesc(category);
            default        -> postRepository
                    .findByCategoryAndIsPublicTrueAndDeletedAtIsNullOrderByCreatedAtDesc(category);
        };

        return posts.stream()
                .map(post -> {
                    long likeCount = likeRepository.countByPostAndDeletedAtIsNull(post);
                    return PostListResponse.from(post, likeCount);
                })
                .collect(Collectors.toList());
    }

    // 글 상세 조회
    @Transactional
    public PostDetailResponse getPost(Long postId, User loginUser) {

        Post post = postRepository.findByPostIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));


        post.increaseViewCount();

        List<Comment> comments = commentRepository.findByPostAndDeletedAtIsNullOrderByCreatedAtAsc(post);
        List<CommentResponse> commentResponses = comments.stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());

        boolean isLiked = false;
        Long likeId = null;

        if (loginUser != null) {
            Optional<Like> like = likeRepository
                    .findByUserAndPostAndDeletedAtIsNull(loginUser, post);
            if (like.isPresent()) {
                isLiked = true;
                likeId = like.get().getLikeId();

            }
        }

        long likeCount = likeRepository.countByPostAndDeletedAtIsNull(post);

        return PostDetailResponse.from(post, likeCount, isLiked, likeId, commentResponses);
    }

    // 글 작성
    @Transactional
    public void createPost(PostCreateRequest request, User loginUser) {

        Course course = null;
        if (request.getCourseId() != null) {
            course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));
        }

        Post post = Post.builder()
                .user(loginUser)
                .course(course)
                .category(request.getCategory())
                .title(request.getTitle())
                .content(request.getContent())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .build();

        postRepository.save(post);

        log.info("[게시글 작성] 게시글이 정상적으로 등록되었습니다. post_id: {}", post.getPostId());
    }

    // 글 수정
    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, User loginUser) {

        Post post = postRepository.findByPostIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        boolean isPublic = request.getIsPublic() != null
                ? request.getIsPublic()
                : post.isPublic();

        post.update(request.getTitle(), request.getContent(), isPublic);

        log.info("[게시글 수정] 게시글이 정상적으로 수정되었습니다. post_id: {}", postId);
    }

    // 글 삭제
    @Transactional
    public void deletePost(Long postId, User loginUser) {

        Post post = postRepository.findByPostIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        boolean isOwner = post.getUser().getId().equals(loginUser.getId());
        boolean isAdmin = loginUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        post.delete();

        log.info("[게시글 삭제] 게시글이 정상적으로 삭제되었습니다. post_id: {}", postId);
    }

    // Q&A 해결 상태 변경
    @Transactional
    public void toggleResolved(Long postId, User loginUser) {

        Post post = postRepository.findByPostIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));


        if (!post.getUser().getId().equals(loginUser.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        post.toggleResolved();

        log.info("[Q&A 해결 상태] 변경 완료. post_id: {}, isResolved: {}", postId, post.isResolved());
    }
}
