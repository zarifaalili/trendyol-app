package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.model.request.ProductRequest;
import org.example.trendyolfinalproject.model.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "previousPrice", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", expression = "java(org.example.trendyolfinalproject.model.enums.Status.ACTIVE)")
    @Mapping(target = "stockQuantity", expression = "java(0)")
    Product toEntity(ProductRequest request);


    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(source = "seller.companyName", target = "sellerCompanyName")
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);


}
