package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.BasketElement;
import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.example.trendyolfinalproject.model.request.BasketElementRequest;
import org.example.trendyolfinalproject.model.response.BasketElementResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BasketElementMapper {



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "basket", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "productVariantId", ignore = true)
//    @Mapping(source = "basketId", target = "basket.id")
    @Mapping(target = "addedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target="quantity", ignore = true)
    BasketElement toEntity(BasketElementRequest request);

    @Mapping(source = "basket.id", target = "basketId")
    @Mapping(source = "productId.id", target = "productId")
    @Mapping(source = "productId.name", target = "productName")
    @Mapping(source = "productId.price", target = "productPrice")
    @Mapping(source = "productVariantId.id", target = "productVariantId")
    @Mapping(source = "productVariantId", target = "productVariantName", qualifiedByName = "mapProductVariantToName")
//    @Mapping(source = "productVariantId.priceAdjustment", target = "productVariantPrice")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(basketElement.getQuantity(), basketElement.getProductId().getPrice(), null))")
    BasketElementResponse toResponse(BasketElement basketElement);


    List<BasketElementResponse> toResponseList(List<BasketElement> basketElements);

    default BigDecimal calculateSubtotal(Integer quantity, BigDecimal productPrice, BigDecimal variantPrice) {
        if (quantity == null || productPrice == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalUnitPrice = productPrice;
        if (variantPrice != null) {
            totalUnitPrice = totalUnitPrice.add(variantPrice);
        }
        return totalUnitPrice.multiply(new BigDecimal(quantity));
    }

    @Named("mapProductVariantToName")
    default String mapProductVariantToName(ProductVariant productVariant) {
        if (productVariant == null) {
            return null;
        }
        return productVariant.getColor() + " - " + productVariant.getSize();
    }

}
