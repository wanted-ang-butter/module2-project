package com.wanted.naeil.domain.course.controller;

import com.wanted.naeil.domain.course.dto.CreateCourseRequest;
import com.wanted.naeil.domain.course.dto.CreateCourseResponse;
import com.wanted.naeil.domain.course.service.InstCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/instructor")
@RequiredArgsConstructor
@Slf4j
public class InstCourseController {

    private final InstCourseService instCourseService;

    // 코스 등록 조회
    @GetMapping("/courses")
    public String CreateCoursePage() {
        log.info(" [Courses] 강의 생성 페이지 조회 성공");
        return "instructor/createCourse";
    }

    /**
     * 코스 등록 페이지, TODO : 추후 강사 Role 확인하는 로직 추가
     * @param request : 코스 생성 시 필요한 컬럼 및 SectionReq
     * @param mv : ModelAndView 호출
     * @return : CreateCoursePage 호출
     */
    @PostMapping("/courses")
    public ModelAndView CreateCoursePage(
            @ModelAttribute CreateCourseRequest request,
            ModelAndView mv) {

        // TODO : @AuthenticationPrincipal 으로 pk 뽑기

        log.info(" [Courses] 강의 생성 시작");

        CreateCourseResponse response =instCourseService.createCourse(request);


        return mv;
    }
}
