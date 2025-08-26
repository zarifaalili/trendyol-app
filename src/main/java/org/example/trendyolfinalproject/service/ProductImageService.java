package org.example.trendyolfinalproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.ProductImage;
import org.example.trendyolfinalproject.dao.repository.ProductImageRepository;
import org.example.trendyolfinalproject.dao.repository.ProductRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.ProductImageMapper;
import org.example.trendyolfinalproject.request.ProductImageCreateRequest;
import org.example.trendyolfinalproject.response.ProductImageResponse;
import org.example.trendyolfinalproject.response.ProductResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;

    public ProductImageResponse addProductImage(Long productId, ProductImageCreateRequest request) {

        log.info("Actionlog.createProductImage.start : productId={}", productId);
        var product = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("Product not found")
        );
        var entity = productImageMapper.toEntity(request);
        entity.setProduct(product);
        ProductImage saved = productImageRepository.save(entity);
        log.info("Actionlog.createProductImage.end : productId={}", productId);
        var mapper=productImageMapper.toResponse(saved);
        return mapper;
    }


}
