package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.BasketElement;
import org.example.trendyolfinalproject.dao.entity.OrderItem;
import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.example.trendyolfinalproject.model.response.OrderItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderId", ignore = true) // Servisdə order set edirsən
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "productVariantId", target = "productVariantId")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(target="createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "unitPrice", expression = "java(basketElement.getProductId().getPrice())")
    OrderItem toEntity(BasketElement basketElement);

    @Mapping(source = "orderId.id", target = "orderId")
    @Mapping(source = "productId.id", target = "productId")
    @Mapping(source = "productId.name", target = "productName")
//    @Mapping(source = "productId.productCode", target = "productCode")
    @Mapping(source = "productVariantId.id", target = "productVariantId")
    @Mapping(source = "productVariantId", target = "productVariantName", qualifiedByName = "mapProductVariantToName")
    @Mapping(target = "subtotal", expression = "java(orderItem.getQuantity() != null && orderItem.getUnitPrice() != null ? orderItem.getQuantity().multiply(orderItem.getUnitPrice()) : java.math.BigDecimal.ZERO)")
    OrderItemResponse toResponse(OrderItem orderItem);

    @Named("mapProductVariantToName")
    default String mapProductVariantToName(ProductVariant productVariant) {
        if (productVariant == null) {
            return null;
        }
        String color = productVariant.getColor() != null ? productVariant.getColor() : "N/A";
        String size = productVariant.getSize() != null ? productVariant.getSize() : "N/A";
        return color + " - " + size;
    }


}
