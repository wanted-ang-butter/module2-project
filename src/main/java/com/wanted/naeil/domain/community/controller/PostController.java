package com.wanted.naeil.domain.community.controller;

import com.wanted.naeil.domain.community.dto.request.PostCreateRequest;
import com.wanted.naeil.domain.community.dto.request.PostUpdateRequest;
import com.wanted.naeil.domain.community.dto.response.PostDetailResponse;
import com.wanted.naeil.domain.community.dto.response.PostListResponse;
import com.wanted.naeil.domain.community.entity.PostCategory;
import com.wanted.naeil.domain.community.service.PostService;
import com.wanted.naeil.domain.course.entity.Course;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final CourseRepository courseRepository;

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
                                   ModelAndView mv) {
        log.info("[게시글 상세] 조회 시작. postId: {}", postId);

        PostDetailResponse post = postService.getPost(postId, null);
        mv.addObject("post", post);
        mv.addObject("category", category);

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
                                 ModelAndView mv) {
        log.info("[게시글 작성 폼] category: {}", category);

        mv.addObject("category", category);

        if (category.equalsIgnoreCase("qna")) {
            // TODO: Security 완성 후 수강 중인 강의로 교체
            List<Course> courses = courseRepository.findAllByOrderByTitleAsc();
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
                                     ModelAndView mv) {
        log.info("[게시글 수정 폼] postId: {}", postId);

        PostDetailResponse post = postService.getPost(postId, null);
        mv.addObject("post", post);
        mv.addObject("category", category);

        if (category.equalsIgnoreCase("qna")) {
            // TODO: Security 완성 후 수강 중인 강의로 교체
            List<Course> courses = courseRepository.findAllByOrderByTitleAsc();
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
                                   ModelAndView mv) {
        log.info("[게시글 작성] 시작. category: {}", category);

        try {
            postService.createPost(request, null);
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
                                   ModelAndView mv) {
        log.info("[게시글 수정] 시작. postId: {}", postId);

        try {
            postService.updatePost(postId, request, null);
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
    @DeleteMapping("/{category}/{postId}")
    public ModelAndView deletePost(@PathVariable String category,
                                   @PathVariable Long postId,
                                   ModelAndView mv) {
        log.info("[게시글 삭제] 시작. postId: {}", postId);

        postService.deletePost(postId, null);
        mv.setViewName("redirect:/community/" + category);
        return mv;
    }

    // Q&A 해결 상태 변경
    @PatchMapping("/{category}/{postId}")
    public ModelAndView toggleResolved(@PathVariable String category,
                                       @PathVariable Long postId,
                                       ModelAndView mv) {
        log.info("[Q&A 해결 상태] 변경 시작. postId: {}", postId);

        postService.toggleResolved(postId, null);
        mv.setViewName("redirect:/community/" + category + "/" + postId);
        return mv;
    }
}