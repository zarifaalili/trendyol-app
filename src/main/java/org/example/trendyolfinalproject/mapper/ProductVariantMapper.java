package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.ProductImage;
import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.example.trendyolfinalproject.request.ProductVariantCreateRequest;
import org.example.trendyolfinalproject.response.ProductVariantResponse;
import org.example.trendyolfinalproject.response.ProductVariantSimpleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true) // Servisdə DB-dən tapıb set edəcəksən
    @Mapping(target = "variantImages", ignore = true)
    ProductVariant toEntity(ProductVariantCreateRequest request);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(target = "imageUrl", source = "variantImages", qualifiedByName = "extractMainImageUrl")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "previousPrice", source = "product.previousPrice")
    ProductVariantResponse toResponse(ProductVariant productVariant);

    @Mapping(target = "imageUrl", source = "variantImages", qualifiedByName = "extractMainImageUrl")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "previousPrice", source = "product.previousPrice")
    ProductVariantSimpleResponse toSimpleResponse(ProductVariant productVariant);

    ProductVariant toVariant(ProductVariantResponse productVariantResponse);

    List<ProductVariantResponse> toResponseList(List<ProductVariant> productVariants);


    @Named("extractMainImageUrl")
    default String extractMainImageUrl(List<ProductImage> variantImages) {
        if (variantImages == null || variantImages.isEmpty()) {
            return null;
        }

        Optional<ProductImage> mainImage = variantImages.stream()
                .filter(ProductImage::getIsMainImage)
                .findFirst();
        return mainImage.map(ProductImage::getImageUrl).orElse(null);
    }

}
