package com.wanted.naeil.domain.course.controller;

import com.wanted.naeil.domain.auth.model.dto.AuthDetails;
import com.wanted.naeil.domain.course.dto.request.CourseCreateRequest;
import com.wanted.naeil.domain.course.dto.response.CreateCourseResponse;
import com.wanted.naeil.domain.course.repository.CategoryRepository;
import com.wanted.naeil.domain.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/instructor/course")
@RequiredArgsConstructor
@Slf4j
public class InstCourseController {

    private final CourseService courseService;
    private final CategoryRepository  categoryRepository;

    // 코스 등록 조회
    @GetMapping
    public ModelAndView CreateCoursePage(ModelAndView mv) {
        log.info(" [Courses] 강의 생성 페이지 조회 시작");

        // TODO : 임시로 리포에서 바로 불러옴, 추후 서비스 로직 호출해서 하는걸로 수정
        mv.addObject("createCourseRequest", new CourseCreateRequest());
        mv.addObject("categories", categoryRepository.findAll());

        mv.setViewName("course/courseCreate");

        return mv;
    }

    /**
     * 코스 등록 페이지, TODO : 추후 강사 Role 확인하는 로직 추가
     * @param request : 코스 생성 시 필요한 컬럼 및 SectionReq
     * @param bindingResult : 검증 결과들
     * @param mv : ModelAndView 호출
     * @return : CreateCoursePage 호출
     */
    @PostMapping
    public ModelAndView CreateCoursePage(
            @AuthenticationPrincipal AuthDetails authDetails,
            @Valid @ModelAttribute CourseCreateRequest request,
            BindingResult bindingResult,
            ModelAndView mv) {
        Long instructorId = authDetails.getLoginUserDTO().getUserId();
        log.info(" [Courses] 강의 생성 시작 강사 이름 : {}, 코스 이름: {}" ,authDetails.getLoginUserDTO().getName(), request.getTitle());

        // 필수값 미입력 예외처리
        if (bindingResult.hasErrors()) {
            log.warn(" [Validation] 강의 등록 검증 실패: {}", bindingResult.getAllErrors());

            mv.addObject("categories", categoryRepository.findAll());
            mv.setViewName("course/courseCreate");
            return mv;
        }

        try {
            CreateCourseResponse response = courseService.createCourse(instructorId, request);
            mv.addObject("message", response.message());
            // mv.setViewName("redirect:/dashboard/instructorDashboard");
            // 임시페이지
            mv.setViewName("redirect:/instructor/course/complete");
        } catch (Exception e) {
            log.error(" [Courses] 강의 생성 중 오류 발생: ", e);
            mv.addObject("errorMessage", "강의 등록 중 오류가 발생했습니다: " + e.getMessage());
            mv.addObject("categories", categoryRepository.findAll());
            mv.setViewName("course/courseCreate");
        }

        return mv;
    }

    // 강의 수정
//    @GetMapping("/{courseId}/edit")

    // 성공시 redirect 페이지
    // TODO : 병합 후 강사의 내 강의 페이지로 수정하기
    @GetMapping("/complete")
    public String showCompletePage() {
        return "course/InstructorCourseComplete";
    }
}
