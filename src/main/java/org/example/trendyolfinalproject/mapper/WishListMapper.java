package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.entity.WishList;
import org.example.trendyolfinalproject.model.request.WishListCreateRequest;
import org.example.trendyolfinalproject.model.response.WishListResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface WishListMapper {



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "productVariant", ignore = true)
    @Mapping(target = "addedAt", expression = "java(java.time.LocalDateTime.now())")
    WishList toEntity(WishListCreateRequest request);


    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user", target = "userName", qualifiedByName = "mapUserToFullName")
    @Mapping(source = "productVariant.id", target = "productVariantId")
    @Mapping(source = "productVariant.product.name", target = "productName")
    @Mapping(source="productVariant.product.price", target = "productPrice")
    @Mapping(source="productVariant.product.previousPrice", target = "previousPrice")
    @Mapping(target = "productImageUrl", expression = "java(getFirstVariantImageUrl(wishList.getProductVariant() != null ? wishList.getProductVariant().getProduct() : null))")
    WishListResponse toResponse(WishList wishList);



    List<WishListResponse> toResponseList(List<WishList> wishLists);

    @Named("mapUserToFullName")
    default String mapUserToFullName(User user) {
        if (user == null) {
            return null;
        }
        String name = Optional.ofNullable(user.getName()).orElse("");
        String surname = Optional.ofNullable(user.getSurname()).orElse("");

        if (name.isEmpty() && surname.isEmpty()) {
            return null;
        }
        return (name + " " + surname).trim();
    }


    default String getFirstVariantImageUrl(Product product) {
        if (product == null || product.getVariants() == null || product.getVariants().isEmpty()) {
            return null;
        }
        return product.getVariants().stream()
                .filter(v -> v.getVariantImages() != null && !v.getVariantImages().isEmpty())
                .findFirst()
                .map(v -> v.getVariantImages().iterator().next().getImageUrl())
                .orElse(null);
    }




}
