package org.example.trendyolfinalproject.controller;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.ProductRequest;
import org.example.trendyolfinalproject.request.ProductVariantCreateRequest;
import org.example.trendyolfinalproject.request.ProductVariantFilterRequest;
import org.example.trendyolfinalproject.response.ProductResponse;
import org.example.trendyolfinalproject.response.ProductVariantDetailResponse;
import org.example.trendyolfinalproject.response.ProductVariantResponse;
import org.example.trendyolfinalproject.service.ProductService;
import org.example.trendyolfinalproject.service.ProductVariantService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductVariantService productVariantService;

    @PostMapping("/createProduct")
//    @PreAuthorize("hasRole('SELLER')")
    ProductResponse createProduct(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping("/getProducts")
    public List<ProductResponse> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/getProductByName/{name}")
    public List<ProductResponse> getProductByName(@PathVariable String name) {
        return productService.getProductByName(name);
    }

    @PatchMapping("/updateProductPrice/{productId}/{newPrice}")
    @PreAuthorize("hasRole('SELLER')")
    public ProductResponse updateProductPrice(@PathVariable Long productId, @PathVariable BigDecimal newPrice) {
        return productService.updateProductPrice(productId, newPrice);
    }


    @GetMapping("/getSellerProducts")
    @PreAuthorize("hasRole('SELLER')")
    public List<ProductResponse> getSellerProducts() {
        return productService.getSellerProducts();
    }


    @GetMapping("/getProductsBetweenDates")
    public List<ProductResponse> getTotalProductsBetweenDates(@PathParam("startDate") LocalDateTime startDate, @PathParam("endDate") LocalDateTime endDate) {
        return productService.getTotalProductsBetweenDates(startDate, endDate);
    }


    @PostMapping("/productVariant/createProductVariant")
    @PreAuthorize("hasRole('SELLER')")
    public ProductVariantResponse createProductVariant(@RequestBody ProductVariantCreateRequest requess) {
        return productVariantService.createProductVariant(requess);
    }

    @PostMapping(value = "/productVariant/addImages/{variantId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasRole('SELLER')")
    public ProductVariantResponse addImagesToVariant(
            @PathVariable Long variantId,
            @RequestPart("images") List<MultipartFile> images) {
        return productVariantService.addImages(variantId, images);
    }


    @DeleteMapping("/productVariant/deleteProductVariant/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public void deleteProductVariant(@PathVariable Long id) {
        productVariantService.deleteProductVariant(id);
    }

    @GetMapping("/productVariant/getProductVariant/{id}")
    public ProductVariantResponse getProductVariant(@PathVariable Long id) {
        return productVariantService.getProductVariant(id);
    }

    @GetMapping("/productVariant/filter")
    public ResponseEntity<List<ProductVariantResponse>> getVariantsByFilter(
            @ModelAttribute ProductVariantFilterRequest filter) {
        return ResponseEntity.ok(productVariantService.getProductVariantsByFilter(filter));
    }

    @GetMapping("/productVariant/getProductVariantDetails/{id}")
    public ProductVariantDetailResponse getProductVariantDetails(@PathVariable Long id) {
        return productVariantService.getProductVariantDetails(id);
    }


    @PatchMapping("/prouctVariant/updateStock/{productVariantId}/{newStock}")
    @PreAuthorize("hasRole('SELLER')")
    public ProductVariantResponse updateProductVariantStock(@PathVariable Long productVariantId, @PathVariable Integer newStock) {
        return productVariantService.updateProductVariantStock(productVariantId, newStock);
    }


}
