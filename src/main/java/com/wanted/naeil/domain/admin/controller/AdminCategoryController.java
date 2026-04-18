package com.wanted.naeil.domain.admin.controller;

import com.wanted.naeil.domain.admin.dto.request.CategoryRequest;
import com.wanted.naeil.domain.admin.dto.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.wanted.naeil.domain.admin.service.AdminCategoryService;


import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
@RestController
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;

    @GetMapping
    public List<CategoryResponse> getCategory() {
        return adminCategoryService.getCategories();
    }

    @PostMapping
    public CategoryResponse createCategory(@RequestBody CategoryRequest request) {
        return
                adminCategoryService.createCategory(request.getName());
    }
    @PatchMapping("/{id}")
    public CategoryResponse
    updateCategory(@PathVariable Long id,@RequestBody CategoryRequest request)
    {
        return
    adminCategoryService.updateCategory(id, request.getName());
    }
    @DeleteMapping("/{id}")
    public void
    deleteCategory(@PathVariable Long id)
    {
        adminCategoryService.deleteCategory(id);
    }

}
