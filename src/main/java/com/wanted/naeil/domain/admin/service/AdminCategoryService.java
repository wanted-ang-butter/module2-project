package com.wanted.naeil.domain.admin.service;

import com.wanted.naeil.domain.admin.dto.response.CategoryResponse;
import com.wanted.naeil.domain.course.entity.Category;
import com.wanted.naeil.domain.course.repository.CategoryRepository;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    // 카테고리 전체 조회
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> CategoryResponse.from(c, courseRepository.countByCategory(c)))
                .toList();
    }

    // 카테고리 생성
    @Transactional
    public CategoryResponse createCategory(String name){
        String trimmedName = name.trim();
        if (categoryRepository.existsByName(trimmedName)) {
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다");
        }
        Category category = Category.builder()
                .name(trimmedName)
                .build();
        return CategoryResponse.from(categoryRepository.save(category), 0L);
    }
    // 카테고리 수정
    @Transactional
    public CategoryResponse updateCategory(Long id, String name) {
        String trimmedName = name.trim();
        Category category = categoryRepository.findById(id).orElseThrow(()-> new NoSuchElementException("카태고리를 찾으 수 없습니다"));
        if (categoryRepository.existsByName(trimmedName) &&
                !category.getName().equals(trimmedName)){
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다");
        }
        category.updateName(trimmedName);
        return CategoryResponse.from(categoryRepository.save(category), 0L);
    }
    // 카테고리 삭제
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("카테고리를 찾을수 없습니다"));
        if (courseRepository.existsByCategoryId(id)) {
            throw new IllegalStateException("해당 카테고리를 사용중인 코스가 있습니다");
        }
        categoryRepository.deleteById(id);
    }

}
