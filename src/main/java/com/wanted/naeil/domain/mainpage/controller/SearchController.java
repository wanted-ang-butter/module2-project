package com.wanted.naeil.domain.mainpage.controller;

import com.wanted.naeil.domain.course.dto.response.CourseListResponse;
import com.wanted.naeil.domain.mainpage.service.MainPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final MainPageService mainPageService;

    @GetMapping("/course/search")
    public String search(@RequestParam(required = false) String q,
                         Model model) {

        log.info("[검색] 키워드: {}", q);

        List<CourseListResponse> searchResults = mainPageService.searchCourses(q);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("keyword", q);

        return "search/searchResult";
    }
}
