package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.CategoryCreateRequest;
import org.example.trendyolfinalproject.response.CategoryResponse;
import org.example.trendyolfinalproject.service.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/createCategory")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(@RequestBody @Valid CategoryCreateRequest request) {
        return categoryService.createCategory(request);
    }

    @GetMapping("/getCategoryById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    private CategoryResponse getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/getCategories")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public List<CategoryResponse> getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping("/getParentCategory")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CategoryResponse> getParentCategory() {
        return categoryService.getParentCategory();
    }


}
