package com.wanted.naeil.domain.course.controller;

import com.wanted.naeil.domain.course.dto.request.CreateCourseRequest;
import com.wanted.naeil.domain.course.dto.response.CreateCourseResponse;
import com.wanted.naeil.domain.course.repository.CategoryRepository;
import com.wanted.naeil.domain.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        mv.addObject("createCourseRequest", new CreateCourseRequest());
        mv.addObject("categories", categoryRepository.findAll());

        mv.setViewName("course/InstructorCourseEdit");

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
            @Valid @ModelAttribute CreateCourseRequest request,
            BindingResult bindingResult,
            ModelAndView mv) {
        // TODO : @AuthenticationPrincipal 으로 pk 뽑기
        log.info(" [Courses] 강의 생성 시작: {} ", request.getTitle());
        if (bindingResult.hasErrors()) {
            log.warn(" [Validation] 강의 등록 검증 실패: {}", bindingResult.getAllErrors());

            mv.addObject("categories", categoryRepository.findAll());
            mv.setViewName("course/InstructorCourseEdit");
            return mv;
        }

        try {
            CreateCourseResponse response = courseService.createCourse(request);
            // TODO : mv.addObject() 으로 값들 꺼내오기
            mv.addObject("message", response.message());
            // TODO : 리다이렉트 임시로 "강사 메인보드" 로 이동
            // mv.setViewName("redirect:/dashboard/instructorDashboard");
            // 임시페이지
            mv.setViewName("redirect:/instructor/course/complete");
        } catch (Exception e) {
            log.error(" [Courses] 강의 생성 중 오류 발생: ", e);
            mv.addObject("errorMessage", "강의 등록 중 오류가 발생했습니다: " + e.getMessage());
            mv.addObject("categories", categoryRepository.findAll());
            mv.setViewName("course/InstructorCourseEdit");
        }

        return mv;
    }

    // 임시페이지
    @GetMapping("/complete")
    public String showCompletePage() {
        return "course/InstructorCourseComplete";
    }
}
