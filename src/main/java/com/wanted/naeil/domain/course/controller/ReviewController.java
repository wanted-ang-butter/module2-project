package com.wanted.naeil.domain.course.controller;

import com.wanted.naeil.domain.course.service.ReviewService;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/my-courses")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;

    // 수강평 작성
    @PostMapping("/{courseId}/reviews")
    public String createReview(@PathVariable Long courseId,
                               @RequestParam Double rating,
                               @RequestParam(required = false) String content,
                               @AuthenticationPrincipal AuthDetails authDetails) {

        log.info("[수강평 작성] courseId: {}", courseId);

        User loginUer = getLoginUser(authDetails);
        reviewService.createReview(courseId, rating, content, loginUer);

        return "redirect:/my-courses";
    }

    // 수강평 수정
    @PostMapping("/reviews/{reviewId}/update")
    public String updateReview(@PathVariable Long reviewId,
                               @RequestParam Double rating,
                               @RequestParam(required = false) String content,
                               @AuthenticationPrincipal AuthDetails authDetails) {

        log.info("[수강평 수정] reviewId: {}", reviewId);

        User loginUser = getLoginUser(authDetails);
        reviewService.updateReview(reviewId, rating, content, loginUser);

        return "redirect:/my-courses";
    }

    // 수강평 삭제
    @PostMapping("/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId,
                               @AuthenticationPrincipal AuthDetails authDetails) {

        log.info("[수강평 삭제] reviewId: {}", reviewId);

        User loginUser = getLoginUser(authDetails);
        reviewService.deleteReview(reviewId, loginUser);

        return "redirect:/my-courses";
    }

    // 로그인 유저 조회 공통 메서드
    private User getLoginUser(AuthDetails authDetails) {
        if (authDetails == null) {
            throw new NoSuchElementException("로그인이 필요합니다.");
        }
        return userRepository.findByUsername(authDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }
}
