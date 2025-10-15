package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.dao.entity.Shipment;
import org.example.trendyolfinalproject.model.enums.Status;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ShipmentHistoryResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ShipmentHistoryService {

    void addShipmentHistory(Shipment shipment);

    void addupdatedShipmentHistory(Shipment shipment, String location, Status status, LocalDateTime timestamp);

    ApiResponse<List<ShipmentHistoryResponse>> getShipmentHistory(Long shipmentId);

    ApiResponse<LocalDateTime> getEstimatedDeliveryDate(Long shipmentId);
}
