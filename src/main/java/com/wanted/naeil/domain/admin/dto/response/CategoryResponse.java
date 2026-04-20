package com.wanted.naeil.domain.admin.dto.response;

import com.wanted.naeil.domain.course.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CategoryResponse {
    private  Long id;
    private String name;
    private LocalDateTime createdAt;
    private long courseCount;


    public  static  CategoryResponse from(Category category, Long courseCount) {
        return  CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .courseCount(courseCount)
                .build();
    }
}
