package com.wanted.naeil.domain.community.controller;

import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import com.wanted.naeil.domain.community.dto.request.PostCreateRequest;
import com.wanted.naeil.domain.community.dto.request.PostUpdateRequest;
import com.wanted.naeil.domain.community.dto.response.PostDetailResponse;
import com.wanted.naeil.domain.community.dto.response.PostListResponse;
import com.wanted.naeil.domain.community.entity.enums.PostCategory;
import com.wanted.naeil.domain.community.service.PostService;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.learning.entity.Enrollment;
import com.wanted.naeil.domain.learning.repository.EnrollmentRepository;
import com.wanted.naeil.domain.user.entity.enums.Role;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import com.wanted.naeil.global.common.exception.CustomException;
import com.wanted.naeil.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    // 글 목록 조회
    @GetMapping("/{category}")
    public ModelAndView postList(@PathVariable String category,
                                 @RequestParam(defaultValue = "latest") String sortType,
                                 ModelAndView mv) {

        log.info("[게시글 목록] 조회 시작. category: {}, sortType: {}", category, sortType);

        PostCategory postCategory = PostCategory.valueOf(category.toUpperCase());
        List<PostListResponse> posts = postService.getPostList(postCategory, sortType);

        mv.addObject("posts", posts);
        mv.addObject("sortType", sortType);
        mv.addObject("category", category);

        if (postCategory == PostCategory.FREE) {
            mv.setViewName("community/freeBoardList");
        } else {
            mv.setViewName("community/QnAList");
        }
        return mv;
    }

    // 글 상세 조회
    @GetMapping("/{category}/{postId}")
    public ModelAndView postDetail(@PathVariable String category,
                                   @PathVariable Long postId,
                                   @AuthenticationPrincipal AuthDetails authDetails,
                                   ModelAndView mv) {

        log.info("[게시글 상세] 조회 시작. postId: {}", postId);

        User loginUser = getLoginUser(authDetails);
        PostDetailResponse post = postService.getPost(postId, loginUser);

        // 본인 여부, 관리자 여부 판단
        boolean isOwner = false;
        boolean isAdmin = false;

        if (loginUser != null){
            isOwner = post.getNickname().equals(loginUser.getNickname());
            isAdmin = loginUser.getRole() == Role.ADMIN;
        }

        mv.addObject("post", post);
        mv.addObject("category", category);
        mv.addObject("isOwner", isOwner);
        mv.addObject("isAdmin", isAdmin);
        mv.addObject("loginUser", loginUser);

        PostCategory postCategory = PostCategory.valueOf(category.toUpperCase());
        if (postCategory == PostCategory.FREE) {
            mv.setViewName("community/freeBoardDetail");
        } else {
            mv.setViewName("community/QnADetail");
        }
        return mv;
    }

    // 글 작성 폼
    @GetMapping("/{category}/form")
    public ModelAndView postForm(@PathVariable String category,
                                 @AuthenticationPrincipal AuthDetails authDetails,
                                 ModelAndView mv) {

        log.info("[게시글 작성 폼] category: {}", category);

        mv.addObject("category", category);

        if (category.equalsIgnoreCase("qna")) {
            User loginUser = getLoginUser(authDetails);
            List<Course> courses = enrollmentRepository.findByUser(loginUser)
                    .stream()
                    .map(Enrollment::getCourse)
                    .collect(Collectors.toList());
            mv.addObject("courses", courses);
            mv.setViewName("community/QnAWrite");
        } else {
            mv.setViewName("community/freeBoardWrite");
        }
        return mv;
    }

    // 글 수정 폼
    @GetMapping("/{category}/{postId}/form")
    public ModelAndView postEditForm(@PathVariable String category,
                                     @PathVariable Long postId,
                                     @AuthenticationPrincipal AuthDetails authDetails,
                                     ModelAndView mv) {
        log.info("[게시글 수정 폼] postId: {}", postId);

        PostDetailResponse post = postService.getPost(postId, null);
        mv.addObject("post", post);
        mv.addObject("category", category);

        if (category.equalsIgnoreCase("qna")) {
            User loginUser = getLoginUser(authDetails);
            List<Course> courses = enrollmentRepository.findByUser(loginUser)
                    .stream()
                    .map(Enrollment::getCourse)
                    .collect(Collectors.toList());
            mv.addObject("courses", courses);
            mv.setViewName("community/QnAWrite");
        } else {
            mv.setViewName("community/freeBoardWrite");
        }
        return mv;
    }

    // 글 작성
    @PostMapping("/{category}")
    public ModelAndView createPost(@PathVariable String category,
                                   @ModelAttribute PostCreateRequest request,
                                   @AuthenticationPrincipal AuthDetails authDetails,
                                   ModelAndView mv) {

        log.info("[게시글 작성] 시작. category: {}", category);

        try {
            User loginUser = getLoginUser(authDetails);
            postService.createPost(request, loginUser);
            mv.setViewName("redirect:/community/" + category);
        } catch (Exception e) {
            log.error("[게시글 작성] 오류 발생: ", e);
            mv.addObject("errorMessage", e.getMessage());
            if (category.equalsIgnoreCase("qna")) {
                mv.setViewName("community/QnAWrite");
            } else {
                mv.setViewName("community/freeBoardWrite");
            }
        }
        return mv;
    }

    // 글 수정
    @PutMapping("/{category}/{postId}")
    public ModelAndView updatePost(@PathVariable String category,
                                   @PathVariable Long postId,
                                   @ModelAttribute PostUpdateRequest request,
                                   @AuthenticationPrincipal AuthDetails authDetails,
                                   ModelAndView mv) {
        log.info("[게시글 수정] 시작. postId: {}", postId);

        try {
            User loginUser = getLoginUser(authDetails);
            postService.updatePost(postId, request, loginUser);
            mv.setViewName("redirect:/community/" + category + "/" + postId);
        } catch (Exception e) {
            log.error("[게시글 수정] 오류 발생: ", e);
            mv.addObject("errorMessage", e.getMessage());
            if (category.equalsIgnoreCase("qna")) {
                mv.setViewName("community/QnAWrite");
            } else {
                mv.setViewName("community/freeBoardWrite");
            }
        }
        return mv;
    }

    // 글 삭제
    @PostMapping("/{category}/{postId}/delete")
    public ModelAndView deletePost(@PathVariable String category,
                                   @PathVariable Long postId,
                                   @AuthenticationPrincipal AuthDetails authDetails,
                                   ModelAndView mv) {

        log.info("[게시글 삭제] 시작. postId: {}", postId);

        User loginUser = getLoginUser(authDetails);
        postService.deletePost(postId, loginUser);
        mv.setViewName("redirect:/community/" + category);
        return mv;
    }

    // Q&A 해결 상태 변경
    @PostMapping("/{category}/{postId}/resolve")
    public ModelAndView toggleResolved(@PathVariable String category,
                                       @PathVariable Long postId,
                                       @AuthenticationPrincipal AuthDetails authDetails,
                                       ModelAndView mv) {

        log.info("[Q&A 해결 상태] 변경 시작. postId: {}", postId);

        User loginUser = getLoginUser(authDetails);
        postService.toggleResolved(postId, loginUser);
        mv.setViewName("redirect:/community/" + category + "/" + postId);
        return mv;
    }

    // 공통 메서드 (로그인 유저 꺼내는)
    private User getLoginUser(AuthDetails authDetails) {
        if (authDetails == null) return null;
        return userRepository.findByUsername(authDetails.getLoginUserDTO().getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}