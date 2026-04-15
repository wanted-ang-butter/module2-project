package com.wanted.naeil.domain.course.dto;

import com.wanted.naeil.domain.course.entity.Category;
import org.springframework.web.multipart.MultipartFile;

public record CreateCourseRequest(
        String title,
        Category category,
        String description,
        int price,
        MultipartFile thumbnail
        //TODO : CreateSectionRequset 생성 후 추가
) {
}
