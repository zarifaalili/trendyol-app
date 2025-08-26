package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.service.ProductImageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/productImage")
@RequiredArgsConstructor
public class ProductImageController {
    private final ProductImageService productImageService;

//    @PostMapping("/createProductImage/{productId}")
//    ProductImageResponse createProductImage(@PathVariable Long productId, @RequestBody ProductImageCreateRequest request) {
//        return productImageService.createProductImage(productId, request);
//    }
}
