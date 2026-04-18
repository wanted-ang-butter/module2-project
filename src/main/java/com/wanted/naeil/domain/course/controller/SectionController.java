package com.wanted.naeil.domain.course.controller;


import com.wanted.naeil.domain.course.dto.response.SectionStudyResponse;
import com.wanted.naeil.domain.course.service.SectionService;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/course")
@RequiredArgsConstructor
@Slf4j
public class SectionController {

    private final SectionService sectionService;

    @GetMapping("/{courseId}/sections/{sectionId}")
    public ModelAndView getSectionStudyPage(
            @AuthenticationPrincipal AuthDetails authDetails,
            @PathVariable Long courseId, @PathVariable Long sectionId,
            ModelAndView mv
    ) {
        Long userId = authDetails.getLoginUserDTO().getUserId();

        SectionStudyResponse response = sectionService.getSectionStudyPage(
                userId, courseId, sectionId);

        mv.addObject("sectionStudy", response);
        mv.setViewName("course/sectionDetail");

        return mv;
    }
}
