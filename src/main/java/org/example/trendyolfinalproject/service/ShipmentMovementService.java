package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.ShipmentMovementCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ShipmentMovementResponse;

public interface ShipmentMovementService {

    ApiResponse<ShipmentMovementResponse> addShipmentMovement(ShipmentMovementCreateRequest request);

}
