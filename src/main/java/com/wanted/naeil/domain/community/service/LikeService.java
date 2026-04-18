package com.wanted.naeil.domain.community.service;

import com.wanted.naeil.domain.community.dto.request.LikeCreateRequest;
import com.wanted.naeil.domain.community.entity.Like;
import com.wanted.naeil.domain.community.entity.Post;
import com.wanted.naeil.domain.community.entity.enums.LikeTargetType;
import com.wanted.naeil.domain.community.repository.LikeRepository;
import com.wanted.naeil.domain.community.repository.PostRepository;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import com.wanted.naeil.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CourseRepository courseRepository;

    // 좋아요 등록
    @Transactional
    public String addLike(LikeCreateRequest request, User loginUser) {

        // 게시글
        if (request.getTargetType() == LikeTargetType.POST) {
            Post post = postRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 게시글입니다."));

            if (likeRepository.findByUserAndPost(loginUser, post).isPresent()) {
                throw new IllegalStateException("이미 좋아요를 등록했습니다.");
            }

            likeRepository.save(Like.builder()
                    .user(loginUser)
                    .targetType(LikeTargetType.POST)
                    .post(post)
                    .build());
            return "/community/" + post.getCategory().name().toLowerCase() + "/" + post.getPostId();
        }

        // 강의
        else if (request.getTargetType() == LikeTargetType.COURSE) {
            Course course = courseRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 강의입니다."));

            if (likeRepository.findByUserAndCourse(loginUser, course).isPresent()) {
                throw new IllegalStateException("이미 좋아요를 등록했습니다.");
            }

            likeRepository.save(Like.builder()
                    .user(loginUser)
                    .targetType(LikeTargetType.COURSE)
                    .course(course)
                    .build());
            return "/courses/" + course.getId();
        } else {
            throw new IllegalArgumentException("지원하지 않는 좋아요 대상입니다.");
        }
    }

    // 좋아요 취소
    @Transactional
    public String deleteLike(Long likeId, User loginUser) {

        Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new NoSuchElementException("해당 좋아요를 찾을 수 없습니다."));

        if (!like.getUser().getId().equals(loginUser.getId())) {
            throw new AccessDeniedException("해당 기능에 대한 접속 권한이 없습니다.");
        }

        String redirectUrl = like.getTargetType() == LikeTargetType.POST
                ? "/community/" + like.getPost().getCategory().name().toLowerCase() + "/" + like.getPost().getPostId()
                : "/courses/" + like.getCourse().getId();

        likeRepository.delete(like);
        return redirectUrl;
    }
}
