package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.CategoryCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    ApiResponse<CategoryResponse> createCategory(CategoryCreateRequest request);

    ApiResponse<CategoryResponse> getCategoryById(Long categoryId);

    ApiResponse<List<CategoryResponse>> getCategories();

    ApiResponse<List<CategoryResponse>> getParentCategory();



}
