package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.ProductQuestion;
import org.example.trendyolfinalproject.model.request.ProductQuestionRequest;
import org.example.trendyolfinalproject.model.response.ProductQuestionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductQuestionMapper {

    @Mapping(target = "id", ignore = true)
    ProductQuestion toEntity(ProductQuestionRequest request);

    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "productName", source = "productVariant.product.name")
    ProductQuestionResponse toResponse(ProductQuestion entity);

    List<ProductQuestionResponse> toResponseList(List<ProductQuestion> entities);




}
