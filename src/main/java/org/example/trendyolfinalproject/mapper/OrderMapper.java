package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Adress;
import org.example.trendyolfinalproject.dao.entity.Order;
import org.example.trendyolfinalproject.model.request.OrderCreateRequest;
import org.example.trendyolfinalproject.model.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "shippingAddressId", ignore = true)
    @Mapping(target = "billingAddressId", ignore = true)
    @Mapping(target = "paymentMethodId", ignore = true)
    @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", expression = "java(org.example.trendyolfinalproject.model.Status.PENDING)")
    @Mapping(target = "trackingNumber", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Order toEntity(OrderCreateRequest request);


    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.surname", target = "userName")
    @Mapping(source = "shippingAddressId.id", target = "shippingAddressId")
    @Mapping(source = "shippingAddressId", target = "shippingAddressDetails", qualifiedByName = "mapAddressToDetails")
    @Mapping(source = "billingAddressId.id", target = "billingAddressId")
    @Mapping(source = "billingAddressId", target = "billingAddressDetails", qualifiedByName = "mapAddressToDetails")
    @Mapping(source = "paymentMethodId.id", target = "paymentMethodId")
    @Mapping(source = "paymentMethodId.cardType", target = "paymentMethodName")
    OrderResponse toResponse(Order order);



    List<OrderResponse> toResponseList(List<Order> orders);


    @Named("mapAddressToDetails")
    default String mapAddressToDetails(Adress address) {
        if (address == null) {
            return null;
        }

        return String.format("%s, %s, %s, %s",
                address.getCity(),
                address.getStreet(),
                address.getZipCode(),
                address.getCountry());
    }


}
