package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.BrandCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.BrandResponse;
import org.example.trendyolfinalproject.service.BrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@RequestBody @Valid BrandCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.createBrand(request));
    }
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(@PathVariable Long id, @RequestBody  BrandCreateRequest request) {
        return ResponseEntity.ok().body(brandService.updateBrand(id, request));
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/{id}")
//    public ApiResponse<String> deleteBrand(@PathVariable Long id) {
//        return brandService.deleteBrand(id);
//    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandbyName(@RequestParam("name") String name) {
        return ResponseEntity.ok().body(brandService.getBrandbyName(name));
    }


}
