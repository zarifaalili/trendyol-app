package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.ShipmentMovementCreateRequest;
import org.example.trendyolfinalproject.response.ShipmentHistoryResponse;
import org.example.trendyolfinalproject.response.ShipmentMovementResponse;
import org.example.trendyolfinalproject.service.ShipmentHistoryService;
import org.example.trendyolfinalproject.service.ShipmentMovementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/shipment")
@RequiredArgsConstructor
public class ShipmentHistoryController {
    private final ShipmentHistoryService shipmentHistoryService;
    private final ShipmentMovementService shipmentMovementService;

    @GetMapping("/hsitory/getShipmentHistory/{shipmentId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<ShipmentHistoryResponse> getShipmentHistory(@PathVariable Long shipmentId) {
        return shipmentHistoryService.getShipmentHistory(shipmentId);
    }


    @GetMapping("/{shipmentId}/getEstimatedDeliveryDate")
    @PreAuthorize("hasRole('CUSTOMER')")
    public LocalDateTime getEstimatedDeliveryDate(@PathVariable Long shipmentId) {
        return shipmentHistoryService.getEstimatedDeliveryDate(shipmentId);
    }

    @PostMapping("/movement/addShipmentMovement")
    @PreAuthorize("hasRole('ADMIN')")
    public ShipmentMovementResponse addShipmentMovement(@RequestBody @Valid ShipmentMovementCreateRequest request) {
        return shipmentMovementService.addShipmentMovement(request);
    }
}
