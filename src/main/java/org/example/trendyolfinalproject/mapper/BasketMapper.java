package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Basket;
import org.example.trendyolfinalproject.request.BasketCreateRequest;
import org.example.trendyolfinalproject.response.BasketResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BasketMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Basket toEntity(BasketCreateRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "discountAmount", target = "discountAmount") // Əlavə etdik
    @Mapping(source = "finalAmount", target = "finalAmount")
    BasketResponse toResponse(Basket basket);

    List<BasketResponse> toResponseList(List<Basket> baskets);

}
