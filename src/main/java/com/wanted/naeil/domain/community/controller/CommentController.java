package com.wanted.naeil.domain.community.controller;

import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import com.wanted.naeil.domain.community.dto.request.CommentCreateRequest;
import com.wanted.naeil.domain.community.dto.request.CommentUpdateRequest;
import com.wanted.naeil.domain.community.service.CommentService;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    // 댓글 작성
    @PostMapping("/{category}/{postId}/comments")
    public ModelAndView createComment(@PathVariable String category,
                                      @PathVariable Long postId,
                                      @ModelAttribute CommentCreateRequest request,
                                      @AuthenticationPrincipal AuthDetails authDetails,
                                      ModelAndView mv) {

        log.info("[댓글 작성] postId: {}", postId);

        User loginUser = getLoginUser(authDetails);
        commentService.createComment(postId, request, loginUser);
        mv.setViewName("redirect:/community/" + category + "/" + postId);

        return mv;
    }

    // 댓글 수정
    @PostMapping("/{category}/{postId}/comments/{commentId}/update")
    public ModelAndView updateComment(@PathVariable String category,
                                      @PathVariable Long postId,
                                      @PathVariable Long commentId,
                                      @ModelAttribute CommentUpdateRequest request,
                                      @AuthenticationPrincipal AuthDetails authDetails,
                                      ModelAndView mv) {

        log.info("[댓글 수정] commentId: {}", commentId);

        User loginUser = getLoginUser(authDetails);
        commentService.updateComment(commentId, request, loginUser);
        mv.setViewName("redirect:/community/" + category + "/" + postId);

        return mv;
    }

    // 댓글 삭제
    @PostMapping("/{category}/{postId}/comments/{commentId}/delete")
    public ModelAndView deleteComment(@PathVariable String category,
                                      @PathVariable Long postId,
                                      @PathVariable Long commentId,
                                      @AuthenticationPrincipal AuthDetails authDetails,
                                      ModelAndView mv) {

        log.info("[댓글 삭제] commentId: {}", commentId);

        User loginUser = getLoginUser(authDetails);
        commentService.deleteComment(commentId, loginUser);
        mv.setViewName("redirect:/community/" + category + "/" + postId);
        return mv;
    }

    // 공통 메서드
    private User getLoginUser(AuthDetails authDetails) {
        if (authDetails == null) return null;
        return userRepository.findByUsername(authDetails.getLoginUserDTO().getUsername())
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));
    }
}
