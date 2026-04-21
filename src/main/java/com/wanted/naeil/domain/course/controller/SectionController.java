package com.wanted.naeil.domain.course.controller;


import com.wanted.naeil.domain.course.dto.request.SectionUpdateRequest;
import com.wanted.naeil.domain.course.dto.request.UploadSectionRequest;
import com.wanted.naeil.domain.course.dto.response.SectionStudyResponse;
import com.wanted.naeil.domain.course.service.SectionService;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SectionController {

    private final SectionService sectionService;

    // 섹션 상세 조회 (섹션 수강)
    @GetMapping("/courses/{courseId}/sections/{sectionId}")
    public ModelAndView getSectionStudyPage(
            @AuthenticationPrincipal AuthDetails authDetails,
            @PathVariable Long courseId, @PathVariable Long sectionId,
            ModelAndView mv
    ) {
        Long userId = authDetails.getLoginUserDTO().getUserId();

        SectionStudyResponse response = sectionService.getSectionStudyPage(
                userId, courseId, sectionId);

        mv.addObject("sectionStudy", response);
        mv.setViewName("courses/sectionDetail");

        return mv;
    }

    // 섹션 수정 - 강사
    @PatchMapping(value = "/instructor/courses/{courseId}/sections/{sectionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Void> updateSection(
            @AuthenticationPrincipal AuthDetails authDetails,
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @Valid @ModelAttribute SectionUpdateRequest request
    ) {
        Long instructorId = authDetails.getLoginUserDTO().getUserId();

        log.info("[SectionUpdate] 섹션 수정 요청 - instructorId: {}, courseId: {}, sectionId: {}",
                instructorId, courseId, sectionId);

        sectionService.updateSection(instructorId, courseId, sectionId, request);
        return ResponseEntity.ok().build();
    }

    // 섹션 추가 - 강사
    @PostMapping("/instructor/courses/{courseId}/sections")
    @ResponseBody
    public ResponseEntity<Void> addSection(
            @AuthenticationPrincipal AuthDetails authDetails,
            @PathVariable Long courseId,
            @Valid @ModelAttribute UploadSectionRequest request
    ) {
        Long instructorId = authDetails.getLoginUserDTO().getUserId();

        log.info("[SectionCreate] 섹션 추가 요청 - instructorId: {}, courseId: {}", instructorId, courseId);

        sectionService.createSection(instructorId, courseId, request);

        return ResponseEntity.ok().build();
    }

    // 섹션 삭제 - 강사
    @DeleteMapping("/instructor/courses/{courseId}/sections/{sectionId}")
    @ResponseBody
    public ResponseEntity<Void> deleteSection(
            @AuthenticationPrincipal AuthDetails authDetails,
            @PathVariable Long courseId,
            @PathVariable Long sectionId
    ) {
        Long instructorId = authDetails.getLoginUserDTO().getUserId();

        log.info("[SectionDelete] 섹션 삭제 요청 - instructorId: {}, courseId: {}, sectionId: {}",
                instructorId, courseId, sectionId);

        sectionService.deleteSection(instructorId, courseId, sectionId);

        return ResponseEntity.ok().build();
    }
}
