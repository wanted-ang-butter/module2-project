package com.wanted.naeil.domain.community.controller;

import com.wanted.naeil.domain.community.dto.request.LikeCreateRequest;
import com.wanted.naeil.domain.community.service.LikeService;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LikeController {

    private final LikeService likeService;
    private final UserRepository userRepository;

    // 좋아요 등록
    @PostMapping("/likes")
    public ModelAndView addLike(@ModelAttribute LikeCreateRequest request,
                                @AuthenticationPrincipal AuthDetails authDetails,
                                ModelAndView mv) {

        log.info("[좋아요 등록] targetType: {}, targetId: {}", request.getTargetType(), request.getTargetId());

        User loginUser = getLoginUser(authDetails);
        String redirectUrl = likeService.addLike(request, loginUser);

        mv.setViewName("redirect: " + redirectUrl);
        return mv;
    }

    // 공통 메서드
    private User getLoginUser(AuthDetails authDetails) {
        if (authDetails == null) return null;
        return userRepository.findByUsername(authDetails.getLoginUserDTO().getUsername())
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));
    }
}
