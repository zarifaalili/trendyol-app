package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.request.SellerCreateRequest;
import org.example.trendyolfinalproject.request.SellerUpdateRequest;
import org.example.trendyolfinalproject.response.SellerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface SellerMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Seller toEntity(SellerCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyName", ignore = true)
    @Mapping(target = "taxId", ignore = true)
    void updateEntity(@MappingTarget Seller seller, SellerUpdateRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user", target = "userName", qualifiedByName = "mapUserToFullName")
    @Mapping(source = "user.email", target = "userEmail")
    SellerResponse toResponse(Seller seller);

    @Named("mapUserToFullName")
    default String mapUserToFullName(User user) {
        if (user == null) return null;
        String name = Optional.ofNullable(user.getName()).orElse("");
        String surname = Optional.ofNullable(user.getSurname()).orElse("");
        return (name + " " + surname).trim();
    }


}
