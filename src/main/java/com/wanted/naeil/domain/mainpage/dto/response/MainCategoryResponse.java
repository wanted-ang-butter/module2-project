package com.wanted.naeil.domain.mainpage.dto.response;

import com.wanted.naeil.domain.course.dto.response.CourseListResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MainCategoryResponse {

    private Long id;
    private String name;
    private List<CourseListResponse> courses;
}
