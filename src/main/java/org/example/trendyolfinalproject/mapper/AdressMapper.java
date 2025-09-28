package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Adress;
import org.example.trendyolfinalproject.model.request.AdressCreateRequest;
import org.example.trendyolfinalproject.model.response.AdressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdressMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "isDefault", ignore = true)
    Adress toEntity(AdressCreateRequest request);

    @Mapping(source = "userId.id", target = "userId")
    AdressResponse toResponse(Adress adress);

    List<AdressResponse> toResponseList(List<Adress> addresses);

}
