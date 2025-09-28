package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Collection;
import org.example.trendyolfinalproject.model.request.CollectionCreateRequest;
import org.example.trendyolfinalproject.model.response.CollectionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CollectionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
//    @Mapping(target = "collectionItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Collection toEntity(CollectionCreateRequest request);

    @Mapping(source = "user.id", target = "userId")
//    @Mapping(source = "collectionItems", target = "items")
    CollectionResponse toResponse(Collection collection);

    List<CollectionResponse> toResponseList(List<Collection> collections);
}