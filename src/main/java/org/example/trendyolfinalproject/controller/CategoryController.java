package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.CategoryCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.CategoryResponse;
import org.example.trendyolfinalproject.service.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryCreateRequest request) {
        return categoryService.createCategory(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ApiResponse<List<CategoryResponse>> getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping("/parents")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CategoryResponse>> getParentCategory() {
        return categoryService.getParentCategory();
    }


}
