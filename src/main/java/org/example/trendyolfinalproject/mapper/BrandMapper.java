package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Brand;
import org.example.trendyolfinalproject.request.BrandCreateRequest;
import org.example.trendyolfinalproject.response.BrandResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BrandMapper {


    @Mapping(target = "id", ignore = true)
    Brand toEntity(BrandCreateRequest request);

    BrandResponse toResponse(Brand brand);

}
