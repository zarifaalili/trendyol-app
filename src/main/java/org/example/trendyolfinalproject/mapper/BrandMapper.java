package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Brand;
import org.example.trendyolfinalproject.model.request.BrandCreateRequest;
import org.example.trendyolfinalproject.model.response.BrandResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BrandMapper {


    @Mapping(target = "id", ignore = true)
    Brand toEntity(BrandCreateRequest request);

    BrandResponse toResponse(Brand brand);

}
