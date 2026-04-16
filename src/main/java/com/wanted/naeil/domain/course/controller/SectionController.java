package com.wanted.naeil.domain.course.controller;

import com.wanted.naeil.domain.course.dto.request.UploadSectionRequest;
import com.wanted.naeil.domain.course.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/instructor")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

}
