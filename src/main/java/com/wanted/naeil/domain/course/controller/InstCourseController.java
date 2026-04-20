package com.wanted.naeil.domain.course.controller;

import com.wanted.naeil.domain.course.dto.request.CourseStatusUpdateRequest;
import com.wanted.naeil.domain.course.dto.request.CourseUpdateRequest;
import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import com.wanted.naeil.domain.course.dto.request.CourseCreateRequest;
import com.wanted.naeil.domain.course.dto.response.CourseEditResponse;
import com.wanted.naeil.domain.course.dto.response.CreateCourseResponse;
import com.wanted.naeil.domain.course.dto.response.InstructorCourseResponse;
import com.wanted.naeil.domain.course.repository.CategoryRepository;
import com.wanted.naeil.domain.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/instructor")
@RequiredArgsConstructor
@Slf4j
public class InstCourseController {

    private final CourseService courseService;
    private final CategoryRepository  categoryRepository;

    // 코스 등록 조회
    @GetMapping("/course")
    public ModelAndView CreateCoursePage(ModelAndView mv,
                                         @AuthenticationPrincipal AuthDetails authDetails) {
        log.info(" [Courses] 강의 생성 페이지 조회 시작");

        // TODO : 임시로 리포에서 바로 불러옴, 추후 서비스 로직 호출해서 하는걸로 수정
        mv.addObject("user", authDetails.getLoginUserDTO());
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
    @PostMapping("/course")
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

    // 내가 등록한 강의 조회
    @GetMapping("/course-management")
    public ModelAndView courseManagementPage(ModelAndView mv,
                                             @AuthenticationPrincipal AuthDetails authDetails) {
        Long instructorId = authDetails.getLoginUserDTO().getUserId();

        List<InstructorCourseResponse> courses =
                courseService.getInstructorCourses(instructorId);
        // 헤더 정보
        mv.addObject("user", authDetails.getLoginUserDTO());
        mv.addObject("courses", courses);
        // TODO : 실시간 강의 등록하면 인자 넣기
        mv.addObject("liveCourses", List.of());
        mv.setViewName("course/InstructorCourseManagement");

        return mv;
    }

    // 강의 수정 페이지 조회
    @GetMapping("/course/{courseId}/edit")
    public ModelAndView EditCoursePage(@AuthenticationPrincipal AuthDetails authDetails,
                                       @PathVariable Long courseId,ModelAndView mv) {

        Long instructorId = authDetails.getLoginUserDTO().getUserId();

        CourseEditResponse response = courseService.getCourseEdit(instructorId, courseId);

        mv.addObject("user", authDetails.getLoginUserDTO());
        mv.addObject("courseEdit", response);
        mv.addObject("categories", categoryRepository.findAll());
        mv.setViewName("course/courseEdit");

        return mv;
    }

    // 강의 수정 기능
    @PatchMapping(value = "/course/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Void> updateCourse(
            @AuthenticationPrincipal AuthDetails authDetails,
            @PathVariable Long courseId,
            @Valid @ModelAttribute CourseUpdateRequest request
            ) {
        Long instructorId = authDetails.getLoginUserDTO().getUserId();

        log.info("[CourseUpdate] 강의 기본 정보 수정 요청 - instructorId: {}, courseId: {}", instructorId, courseId);

        courseService.updateCourse(instructorId, courseId, request);

        return ResponseEntity.ok().build();
    }

    /**
     * 코스 상태 변경 : 활성화 <-> 비활성화
     * @param authDetails : 현재 로그인 정보
     * @param courseId : 수정할 코스
     * @param request : 사용자의 입력 상태값
     * @return : ResponseEntity여서 상태만 변경
     */
    @PatchMapping(value = "/course/{courseId}/status", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Void> updateCourseStatus(
            @AuthenticationPrincipal AuthDetails authDetails,
            @PathVariable Long courseId,
            @Valid @ModelAttribute CourseStatusUpdateRequest request
    ) {
        Long instructorId = authDetails.getLoginUserDTO().getUserId();

        log.info("[CourseStatusUpdate] 강의 상태 변경 요청 - instructorId: {}, courseId: {}, status: {}",
                instructorId, courseId, request.getStatus());

        courseService.updateCourseStatus(instructorId, courseId, request);

        return ResponseEntity.ok().build();
    }

    /**
     * 코스 등록 요청 상대 변경 : 승인 대기 <-> 승인 대기 요청 취소
     * @param authDetails
     * @param courseId
     * @param request : 사용자 입력 상태
     * @return : ResponseEntity여서 상태만 변경
     */
    @PatchMapping("/course/{courseId}/registration-status")
    @ResponseBody
    public ResponseEntity<Void> cancelCourseRegistration(
            @AuthenticationPrincipal AuthDetails authDetails,
            @PathVariable Long courseId,
            @Valid @ModelAttribute CourseStatusUpdateRequest request
    ) {
        Long instructorId = authDetails.getLoginUserDTO().getUserId();

        log.info("[CourseCancelRequest] 강의 등록 요청 취소 요청 - instructorId: {}, courseId: {}",
                instructorId, courseId);

        courseService.updateCourseRegistrationStatus(instructorId, courseId, request.getStatus());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/course/{courseId}/delete-request")
    public String requestCourseDelete(
            @AuthenticationPrincipal AuthDetails authDetails,
            @PathVariable Long courseId,
            RedirectAttributes redirectAttributes
    ) {
        Long instructorId = authDetails.getLoginUserDTO().getUserId();

        courseService.requestCourseDelete(instructorId, courseId);

        redirectAttributes.addFlashAttribute("message", "강의 삭제 요청이 접수되었습니다.");
        return "redirect:/instructor/course-management";
    }
    

    // 성공시 redirect 페이지
    // TODO : 병합 후 강사의 내 강의 페이지로 수정하기
    @GetMapping("/course/complete")
    public String showCompletePage() {
        return "course/InstructorCourseComplete";
    }
}
