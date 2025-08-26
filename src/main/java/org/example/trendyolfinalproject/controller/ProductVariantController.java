package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.ProductVariantCreateRequest;
import org.example.trendyolfinalproject.request.ProductVariantFilterRequest;
import org.example.trendyolfinalproject.response.ProductVariantDetailResponse;
import org.example.trendyolfinalproject.response.ProductVariantResponse;
import org.example.trendyolfinalproject.service.ProductVariantService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/productVariant")
@RequiredArgsConstructor
public class ProductVariantController {
    private final ProductVariantService productVariantService;
//
//    @PostMapping("/v1/productVariant/createProductVariant")
//    @PreAuthorize("hasRole('SELLER')")
//    public ProductVariantResponse createProductVariant(@RequestBody ProductVariantCreateRequest requess) {
//        return productVariantService.createProductVariant(requess);
//    }
//
//    @PostMapping(value = "/addImages/{variantId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
////    @PreAuthorize("hasRole('SELLER')")
//    public ProductVariantResponse addImagesToVariant(
//            @PathVariable Long variantId,
//            @RequestPart("images") List<MultipartFile> images) {
//        return productVariantService.addImages(variantId, images);
//    }
//
//
//    @DeleteMapping("/deleteProductVariant/{id}")
//    @PreAuthorize("hasRole('SELLER')")
//    public void deleteProductVariant(@PathVariable Long id) {
//        productVariantService.deleteProductVariant(id);
//    }
//
//    @GetMapping("/getProductVariant/{id}")
//    public ProductVariantResponse getProductVariant(@PathVariable Long id) {
//        return productVariantService.getProductVariant(id);
//    }
//
//    @GetMapping("/filter")
//    public ResponseEntity<List<ProductVariantResponse>> getVariantsByFilter(
//            @ModelAttribute ProductVariantFilterRequest filter) {
//        return ResponseEntity.ok(productVariantService.getProductVariantsByFilter(filter));
//    }
//
//    @GetMapping("/getProductVariantDetails/{id}")
//    public ProductVariantDetailResponse getProductVariantDetails(@PathVariable Long id) {
//        return productVariantService.getProductVariantDetails(id);
//    }

}
