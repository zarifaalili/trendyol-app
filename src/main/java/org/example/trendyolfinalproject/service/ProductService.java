package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.ProductRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    ApiResponse<ProductResponse> createProduct(ProductRequest request);

    ApiResponse<Page<ProductResponse>> getProducts(int page, int size);

    ApiResponse<List<ProductResponse>> getProductByName(String name);

    ApiResponse<ProductResponse> updateProductPrice(Long productId, BigDecimal newPrice);

    ApiResponse<List<ProductResponse>> getSellerProducts();

    ApiResponse<Page<ProductResponse>> getTotalProductsBetweenDates(  String startDateStr, String endDateStr, int page, int size);





}
