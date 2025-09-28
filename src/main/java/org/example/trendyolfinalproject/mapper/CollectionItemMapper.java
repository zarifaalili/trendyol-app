package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.CollectionItem;
import org.example.trendyolfinalproject.model.request.CollectionItemFromWishListRequest;
import org.example.trendyolfinalproject.model.request.CollectionItemRequest;
import org.example.trendyolfinalproject.model.response.CollectionItemFromWishListResponse;
import org.example.trendyolfinalproject.model.response.CollectionItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface CollectionItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "collection", ignore = true)
    @Mapping(source = "productVariantId", target = "productVariant.id") // düzəliş
    @Mapping(target = "addedAt", ignore = true)
    CollectionItem toEntity(CollectionItemRequest request);

    @Mapping(source = "productVariant.id", target = "productVariantId")
    @Mapping(source = "productVariant.product.name", target = "productName")
    CollectionItemResponse toResponse(CollectionItem item);


    List<CollectionItemResponse> toResponseList(Set<CollectionItem> items);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "productVariant.id", target = "productVariantId")
    @Mapping(source = "productVariant.product.name", target = "productName")
    @Mapping(source = "addedAt", target = "addedAt")
    CollectionItemFromWishListResponse toFromWishListResponse(CollectionItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "collection.id", source = "collectionId")
    @Mapping(target = "collection", ignore = true)
    @Mapping(target = "productVariant", ignore = true)
    @Mapping(target = "addedAt", ignore = true)
    CollectionItem toEntityFromWishListRequest(CollectionItemFromWishListRequest request);
}
