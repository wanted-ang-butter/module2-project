package com.wanted.naeil.domain.admin.controller;

import com.wanted.naeil.domain.admin.service.AdminCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    // 카테고리 관리 페이지 - 전체 카테고리 목록 조회
    @GetMapping("/category-management")
    public String category(Model model) {
        model.addAttribute("categories", adminCategoryService.getCategories());
        return "admin/category";
    }

    // 카테고리 추가
    @PostMapping("/category-management")
    public String createCategory(@RequestParam String name) {
        adminCategoryService.createCategory(name);
        return "redirect:/admin/category-management";
    }

    // 카테고리 수정
    @PostMapping("/category-management/{id}/edit")
    public String updateCategory(@PathVariable Long id, @RequestParam String name) {
        adminCategoryService.updateCategory(id, name);
        return "redirect:/admin/category-management";
    }

    // 카테고리 삭제
    @PostMapping("/category-management/{id}/delete")
    public String deleteCategory(@PathVariable Long id) {
        adminCategoryService.deleteCategory(id);
        return "redirect:/admin/category-management";
    }

}
