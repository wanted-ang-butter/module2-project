package com.wanted.naeil.domain.admin.dto.response;

import com.wanted.naeil.domain.course.entity.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {
    private  Long id;
    private String name;

    public  static  CategoryResponse from(Category category) {
        return  CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
