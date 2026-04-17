package com.wanted.naeil.domain.admin.service;

import com.wanted.naeil.domain.admin.dto.response.CategoryResponse;
import com.wanted.naeil.domain.course.entity.Category;
import com.wanted.naeil.domain.course.repository.CategoryRepository;
import com.wanted.naeil.domain.course.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }
    @Transactional
    public CategoryResponse createCategory(String name){
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 카테고라입니다");
        }
        Category category = Category.builder()
                .name(name)
                .build();
        return
                CategoryResponse.from(categoryRepository.save(category));
    }
    @Transactional
    public CategoryResponse updateCategory(Long id, String name) {
        Category category = categoryRepository.findById(id).orElseThrow(()-> new NoSuchElementException("카태고리를 찾으 수 없습니다"));
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다");
        }
        category.updateName(name);
        return CategoryResponse.from(category);
    }
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
