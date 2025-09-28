package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.ReviewImage;
import org.example.trendyolfinalproject.model.request.ReviewImageCreateRequest;
import org.example.trendyolfinalproject.model.response.ReviewImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewImageMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "review", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    ReviewImage toEntity(ReviewImageCreateRequest request);


    @Mapping(source = "review.id", target = "reviewId")
    ReviewImageResponse toResponse(ReviewImage reviewImage);




}
