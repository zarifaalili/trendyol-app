package org.example.trendyolfinalproject.mapper;


import org.example.trendyolfinalproject.dao.entity.ShipmentMovement;
import org.example.trendyolfinalproject.model.request.ShipmentMovementCreateRequest;
import org.example.trendyolfinalproject.model.response.ShipmentMovementResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShipmentMovementMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shipment", ignore = true)

    ShipmentMovement toEntity(ShipmentMovementCreateRequest request);

    @Mapping(source = "shipment.id", target = "shipmentId")
    ShipmentMovementResponse toResponse(ShipmentMovement entity);
}

