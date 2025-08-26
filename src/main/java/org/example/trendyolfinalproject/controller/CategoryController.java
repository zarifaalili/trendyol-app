package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.CategoryCreateRequest;
import org.example.trendyolfinalproject.response.ApiResponse;
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
    public ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryCreateRequest request) {
        return categoryService.createCategory(request);
    }

    @GetMapping("/getCategoryById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    private ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/getCategories")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ApiResponse<List<CategoryResponse>> getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping("/getParentCategory")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CategoryResponse>> getParentCategory() {
        return categoryService.getParentCategory();
    }


}
