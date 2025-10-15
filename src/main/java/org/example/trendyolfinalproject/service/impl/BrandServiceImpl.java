package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.repository.BrandRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.exception.customExceptions.AlreadyException;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.BrandMapper;
import org.example.trendyolfinalproject.model.request.BrandCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.BrandResponse;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.BrandService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;


    @Override
    public ApiResponse<BrandResponse> createBrand(BrandCreateRequest request) {
        log.info("Actionlog.createBrand.start : name={}", request.getName());
        Long userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (brandRepository.existsByName(request.getName())) {
            throw new AlreadyException("Brand name already exists : " + request.getName());
        }
        var brandEntity = brandMapper.toEntity(request);
        var saved = brandRepository.save(brandEntity);
        var response = brandMapper.toResponse(saved);
        log.info("Actionlog.createBrand.end : name={}", request.getName());
        auditLogService.createAuditLog(user, "Create Brand", "Brand created successfully. Brand id: " + saved.getName());

        return ApiResponse.<BrandResponse>builder()
                .status(200)
                .message("Brand created successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<String> deleteBrand(Long id) {
        log.info("Actionlog.deleteBrand.start : id={}", id);
        Long userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        var brand = brandRepository.findById(id).orElseThrow(() -> new NotFoundException("Brand not found : " + id));
        brandRepository.deleteById(id);
        auditLogService.createAuditLog(user, "Delete Brand", "Brand deleted successfully. Brand id: " + brand.getName());
        log.info("Actionlog.deleteBrand.end : id={}", id);

        return ApiResponse.<String>builder()
                .status(200)
                .message("Brand deleted successfully")
                .data("Brand with id " + id + " deleted successfully")
                .build();
    }

    @Override
    public ApiResponse<BrandResponse> updateBrand(Long id, BrandCreateRequest request) {
        log.info("Actionlog.updateBrand.start : id={}", id);
        Long userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        var brand = brandRepository.findById(id).orElseThrow(() -> new NotFoundException("Brand not found : " + id));

        if (request.getName() != null) brand.setName(request.getName());
        if (request.getDescription() != null) brand.setDescription(request.getDescription());
        var saved = brandRepository.save(brand);
        var response = brandMapper.toResponse(saved);
        auditLogService.createAuditLog(user, "Update Brand", "Brand updated successfully. Brand id: " + saved.getName());
        log.info("Actionlog.updateBrand.end : id={}", id);
        return ApiResponse.<BrandResponse>builder()
                .status(200)
                .message("Brand updated successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<BrandResponse> getBrandbyName(String name) {
        log.info("Actionlog.getBrandbyName.start : name={}", name);
        Long userId = getCurrentUserId();

        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        var brand = brandRepository.findBrandByName(name).orElseThrow(() -> new NotFoundException("Brand not found : " + name));
        var response = brandMapper.toResponse(brand);
        auditLogService.createAuditLog(user, "Get Brand by name", "Get Brand by name successfully. Brand id: " + brand.getId());
        log.info("Actionlog.getBrandbyName.end : name={}", name);
        return ApiResponse.<BrandResponse>builder()
                .status(200)
                .message("Brand retrieved successfully")
                .data(response)
                .build();
    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }

}
