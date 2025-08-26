//package org.example.trendyolfinalproject.mapper;
//
//import org.example.trendyolfinalproject.dao.entity.Shipment;
//import org.example.trendyolfinalproject.request.ShipmentCreateRequest;
//import org.example.trendyolfinalproject.response.ShipmentResponse;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper(componentModel = "spring")
//public interface ShipmentMapper {
//
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "order", ignore = true)
//    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
//    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
//    Shipment toEntity(ShipmentCreateRequest request);
//
//
//    @Mapping(source = "order.id", target = "orderId")
//    ShipmentResponse toResponse(Shipment shipment);
//
//
//
//}
