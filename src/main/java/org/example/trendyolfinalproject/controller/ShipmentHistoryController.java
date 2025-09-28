package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.ShipmentMovementCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ShipmentHistoryResponse;
import org.example.trendyolfinalproject.model.response.ShipmentMovementResponse;
import org.example.trendyolfinalproject.service.ShipmentHistoryService;
import org.example.trendyolfinalproject.service.ShipmentMovementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/shipments")
@RequiredArgsConstructor
public class ShipmentHistoryController {
    private final ShipmentHistoryService shipmentHistoryService;
    private final ShipmentMovementService shipmentMovementService;

    @GetMapping("/{shipmentId}/history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<ShipmentHistoryResponse>> getShipmentHistory(@PathVariable Long shipmentId) {
        return shipmentHistoryService.getShipmentHistory(shipmentId);
    }


    @GetMapping("/{shipmentId}/estimated-delivery")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<LocalDateTime> getEstimatedDeliveryDate(@PathVariable Long shipmentId) {
        return shipmentHistoryService.getEstimatedDeliveryDate(shipmentId);
    }

    @PostMapping("/movement")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ShipmentMovementResponse> addShipmentMovement(@RequestBody @Valid ShipmentMovementCreateRequest request) {
        return shipmentMovementService.addShipmentMovement(request);
    }
}
