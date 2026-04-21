package com.wanted.naeil.global.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;

import static com.fasterxml.jackson.databind.util.ClassUtil.getRootCause;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String DEFAULT_ERROR_VIEW = "error/generic-error";

    // 파일 업로드 용량 초과 에러
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ModelAndView handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("파일 용량 초과 : {}", e.getMessage());

        ModelAndView mv = new ModelAndView();
        mv.addObject("errorMessage", "업로드 가능한 최대 용량을 초과했습니다. 파일 크기를 확인해주세요.");
        mv.addObject("status", 413);
        mv.setViewName(DEFAULT_ERROR_VIEW);
        return mv;
    }

    // 400 에러, 잘못된 요청
    @ExceptionHandler(IllegalArgumentException.class)
    protected ModelAndView handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("잘못된 요청 : {}", e.getMessage());

        ModelAndView mv = new ModelAndView();
        mv.addObject("errorMessage", e.getMessage());
        mv.addObject("status", 400);
        mv.setViewName(DEFAULT_ERROR_VIEW);
        return mv;
    }

    // 404 에러, 리소스 not Found
    @ExceptionHandler(NoResourceFoundException.class)
    protected ModelAndView handleNoResourceFoundException(
            NoResourceFoundException e,
            HttpServletRequest request
    ) {
        String requestUri = request.getRequestURI();

        log.warn("[Static Resource Not Found] URI: {}, Message: {}", requestUri, e.getMessage());

        ModelAndView mv = new ModelAndView();
        mv.addObject("errorMessage", "요청한 리소스를 찾을 수 없습니다.");
        mv.addObject("status", 404);
        mv.setViewName(DEFAULT_ERROR_VIEW);
        return mv;
    }

    // 404 에러, 데이터 not found
    @ExceptionHandler(NoSuchElementException.class)
    protected ModelAndView handleNoSuchElementException(NoSuchElementException e) {
        log.warn("데이터 미존재: {}", e.getMessage());

        ModelAndView mv = new ModelAndView();
        mv.addObject("errorMessage", e.getMessage());
        mv.addObject("status", 404);
        mv.setViewName(DEFAULT_ERROR_VIEW);
        return mv;
    }
    // 승재, 409 중복키 에러
    @ExceptionHandler(DuplicateKeyException.class)
    protected ModelAndView handleDuplicateKeyException(DuplicateKeyException e) {
        log.warn("중복 데이터 : {}", e.getMessage());
        ModelAndView mv = new ModelAndView();
        mv.addObject("errorMessage", e.getMessage());
        mv.addObject("status", 409);
        mv.setViewName(DEFAULT_ERROR_VIEW);
        return mv;
    }

    // 409 에러, 중복 요청 성민(추가)
    @ExceptionHandler(IllegalStateException.class)
    protected ModelAndView handleIllegalStateException(IllegalStateException e) {
        log.warn("중복 요청 : {}", e.getMessage());

        ModelAndView mv = new ModelAndView();
        mv.addObject("errorMessage", e.getMessage());
        mv.addObject("status", 409);
        mv.setViewName(DEFAULT_ERROR_VIEW);
        return mv;
    }


    // 403 에러, 권한 없음 (글 수정/삭제 시 본인이 아닌 경우)
    @ExceptionHandler(AccessDeniedException.class)
    protected ModelAndView handleAccessDeniedException(AccessDeniedException e) {
        log.warn("권한 없음 : {}", e.getMessage());

        ModelAndView mv = new ModelAndView();
        mv.addObject("errorMessage", e.getMessage());
        mv.addObject("status", 403);
        mv.setViewName(DEFAULT_ERROR_VIEW);
        return mv;
    }

    // 최상위 500 에러, 알 수 없는 에러
    @ExceptionHandler(Exception.class)
    protected ModelAndView handleException(Exception e, HttpServletRequest request) {

        Throwable rootCause = getRootCause(e);
        String rootClassName = rootCause.getClass().getSimpleName();
        String rootMessage = rootCause.getMessage();
        String requestUri = request.getRequestURI();

        log.error("🚨[Server Internal Error] URI: {}, RootCause: {}, Message: {}",
                requestUri, rootClassName, rootMessage, e);

        ModelAndView mv = new ModelAndView();
        mv.addObject("errorMessage", "시스템 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
        mv.addObject("status", 500);
        mv.setViewName(DEFAULT_ERROR_VIEW);
        return mv;
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable.getCause();
        // cause가 null이 될 때까지 재귀 호출하여 가장 깊은 곳의 에러를 찾습니다.
        return (cause != null) ? getRootCause(cause) : throwable;
    }



}
