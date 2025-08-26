package org.example.trendyolfinalproject.mapper;

import jdk.dynalink.linker.LinkerServices;
import org.example.trendyolfinalproject.dao.entity.ShipmentHistory;
import org.example.trendyolfinalproject.request.ShipmentHistoryCreateRequest;
import org.example.trendyolfinalproject.response.ShipmentHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ShipmentHistoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shipment", ignore = true)
    ShipmentHistory toEntity(ShipmentHistoryCreateRequest request);


    @Mapping(source = "shipment.id", target = "shipmentId")
    ShipmentHistoryResponse toResponse(ShipmentHistory shipmentHistory);

    List<ShipmentHistoryResponse> toResponseList(List<ShipmentHistory> shipmentHistories);


}
