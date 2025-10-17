package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.ProductRequest;
import org.example.trendyolfinalproject.model.request.ProductVariantCreateRequest;
import org.example.trendyolfinalproject.model.request.ProductVariantFilterRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ProductResponse;
import org.example.trendyolfinalproject.model.response.ProductVariantDetailResponse;
import org.example.trendyolfinalproject.model.response.ProductVariantResponse;
import org.example.trendyolfinalproject.service.ProductService;
import org.example.trendyolfinalproject.service.ProductVariantService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductVariantService productVariantService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody @Valid ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        return ResponseEntity.ok().body(productService.getProducts(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductByName(@RequestParam String name) {
        return ResponseEntity.ok().body(productService.getProductByName(name));
    }

    @PatchMapping("/update/{productId}/price")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductPrice(@PathVariable Long productId, @RequestParam BigDecimal newPrice) {
        return ResponseEntity.ok().body(productService.updateProductPrice(productId, newPrice));
    }


    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getSellerProducts() {
        return ResponseEntity.ok().body(productService.getSellerProducts());
    }


    @GetMapping("/between-dates")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getTotalProductsBetweenDates(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
            ) {
        return ResponseEntity.ok().body(productService.getTotalProductsBetweenDates(startDate, endDate, page, size));
    }


    @PostMapping("/variants")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> createProductVariant(@RequestBody @Valid ProductVariantCreateRequest requess) {
        return ResponseEntity.ok().body(productVariantService.createProductVariant(requess));
    }

    @PostMapping(value = "/variants/{variantId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> addImagesToVariant(
            @PathVariable Long variantId,
            @RequestPart("images") List<MultipartFile> images) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productVariantService.addImages(variantId, images));
    }


    @GetMapping("/variants/{id}")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> getProductVariant(@PathVariable Long id) {
        return ResponseEntity.ok().body(productVariantService.getProductVariant(id));
    }

    @GetMapping("/variants/filter")
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getVariantsByFilter(
            @ModelAttribute ProductVariantFilterRequest filter) {
        return ResponseEntity.ok().body(productVariantService.getProductVariantsByFilter(filter));
    }

    @GetMapping("/variants/{id}/details")
    public ResponseEntity<ApiResponse<ProductVariantDetailResponse>> getProductVariantDetails(@PathVariable Long id) {
        return ResponseEntity.ok().body(productVariantService.getProductVariantDetails(id));
    }


    @PatchMapping("/variants/{productVariantId}/stock")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> updateProductVariantStock(@PathVariable Long productVariantId, @RequestParam Integer newStock) {
        return ResponseEntity.ok().body(productVariantService.updateProductVariantStock(productVariantId, newStock));
    }


}
