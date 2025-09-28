package org.example.trendyolfinalproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.AuditLog;
import org.example.trendyolfinalproject.dao.entity.Category;
import org.example.trendyolfinalproject.dao.repository.AuditLogRepository;
import org.example.trendyolfinalproject.dao.repository.CategoryRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.CategoryMapper;
import org.example.trendyolfinalproject.model.request.CategoryCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.CategoryResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AuditLogRepository auditLogRepository;


    public ApiResponse<CategoryResponse> createCategory(CategoryCreateRequest request) {

        Category category = categoryMapper.toEntity(request);

        if (request.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(request.getParentCategoryId()).orElseThrow(
                    () -> new NotFoundException("Parent category not found")
            );
            category.setParentCategory(parent);

        }


        var saved = categoryRepository.save(category);
        var response = categoryMapper.toResponse(saved);
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(null);
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLog.setAction("Create Category");
        auditLog.setDetails("Category created successfully. Category id: " + saved.getId());
        auditLogRepository.save(auditLog);

        return ApiResponse.<CategoryResponse>builder()
                .status(200)
                .message("Category created successfully")
                .data(response)
                .build();

    }

    public ApiResponse<CategoryResponse> getCategoryById(Long categoryId) {
        log.info("Actionlog.getCategory.start : categoryId={}", categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        CategoryResponse response = categoryMapper.toResponse(category);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(null);
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLog.setAction("Get Category by id");
        auditLog.setDetails("Get Category by id successfully. Category id: " + category.getId());
        auditLogRepository.save(auditLog);

        log.info("Actionlog.getCategory.end : categoryId={}", categoryId);
        return ApiResponse.<CategoryResponse>builder()
                .status(200)
                .message("Category fetched successfully")
                .data(response)
                .build();
    }


    public ApiResponse<List<CategoryResponse>> getCategories() {
        log.info("Actionlog.getCategories.start : ");
        List<CategoryResponse> response = categoryMapper.toResponseList(categoryRepository.findAll());

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(null);
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLog.setAction("Get Categories");
        auditLog.setDetails("Get Categories successfully. ");
        auditLogRepository.save(auditLog);

        log.info("Actionlog.getCategories.end : ");
        return ApiResponse.<List<CategoryResponse>>builder()
                .status(200)
                .message("Categories fetched successfully")
                .data(response)
                .build();
    }

    public ApiResponse<List<CategoryResponse>> getParentCategory() {
        log.info("Actionlog.getParentCategory.start : ");
        List<Category> parent = categoryRepository.findAllParentCategories();

        if (parent.isEmpty()) {
            throw new NotFoundException("Parent category not found");
        }
        var response = categoryMapper.toResponseList(parent);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(null);
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLog.setAction("Get Parent Categories");
        auditLog.setDetails("Get Parent Categories successfully. ");
        auditLogRepository.save(auditLog);

        log.info("Actionlog.getParentCategory.end : ");
        return ApiResponse.<List<CategoryResponse>>builder()
                .status(200)
                .message("Parent categories fetched successfully")
                .data(response)
                .build();
    }

}
