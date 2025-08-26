package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.ProductImage;
import org.example.trendyolfinalproject.request.ProductImageCreateRequest;
import org.example.trendyolfinalproject.response.ProductImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target="productVariant", ignore = true)
    ProductImage toEntity(ProductImageCreateRequest request);

    @Mapping(source ="product.id", target = "productId" )
    ProductImageResponse toResponse(ProductImage productImage);

}
