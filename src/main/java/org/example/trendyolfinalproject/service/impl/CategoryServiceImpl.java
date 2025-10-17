package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Category;
import org.example.trendyolfinalproject.dao.repository.CategoryRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.CategoryMapper;
import org.example.trendyolfinalproject.model.request.CategoryCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.CategoryResponse;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AuditLogService auditLogService;

    @Override
    public ApiResponse<CategoryResponse> createCategory(CategoryCreateRequest request) {
        log.info("Actionlog.createCategory.start : ");
        Category category = categoryMapper.toEntity(request);
        if (request.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(request.getParentCategoryId()).orElseThrow(
                    () -> new NotFoundException("Parent category not found")
            );
            category.setParentCategory(parent);
        }
        var saved = categoryRepository.save(category);
        var response = categoryMapper.toResponse(saved);
        auditLogService.createAuditLog(null, "Create Category", "Create Category successfully. Category id: " + saved.getId());
        log.info("Actionlog.createCategory.end : ");
        return ApiResponse.<CategoryResponse>builder()
                .status(201)
                .message("Category created successfully")
                .data(response)
                .build();

    }

    @Override
    public ApiResponse<CategoryResponse> getCategoryById(Long categoryId) {
        log.info("Actionlog.getCategory.start : categoryId={}", categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        CategoryResponse response = categoryMapper.toResponse(category);

        auditLogService.createAuditLog(null, "Get Category by id", "Get Category by id successfully. Category id: " + category.getId());

        log.info("Actionlog.getCategory.end : categoryId={}", categoryId);
        return ApiResponse.<CategoryResponse>builder()
                .status(200)
                .message("Category fetched successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<List<CategoryResponse>> getCategories() {
        log.info("Actionlog.getCategories.start : ");
        List<CategoryResponse> response = categoryMapper.toResponseList(categoryRepository.findAll());
        auditLogService.createAuditLog(null, "Get Categories", "Get Categories successfully. ");

        log.info("Actionlog.getCategories.end : ");
        return ApiResponse.<List<CategoryResponse>>builder()
                .status(200)
                .message("Categories fetched successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<List<CategoryResponse>> getParentCategory() {
        log.info("Actionlog.getParentCategory.start : ");
        List<Category> parent = categoryRepository.findAllParentCategories();
        if (parent.isEmpty()) {
            log.error("Parent category not found");
            throw new NotFoundException("Parent category not found");
        }
        var response = categoryMapper.toResponseList(parent);

        auditLogService.createAuditLog(null, "Get Parent Categories", "Get Parent Categories successfully. ");

        log.info("Actionlog.getParentCategory.end : ");
        return ApiResponse.<List<CategoryResponse>>builder()
                .status(200)
                .message("Parent categories fetched successfully")
                .data(response)
                .build();
    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }

}
