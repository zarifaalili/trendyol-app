//package org.example.trendyolfinalproject.controller;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.example.trendyolfinalproject.request.ShipmentMovementCreateRequest;
//import org.example.trendyolfinalproject.response.ShipmentMovementResponse;
//import org.example.trendyolfinalproject.service.ShipmentMovementService;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/v1/shipment-movement")
//@RequiredArgsConstructor
//public class ShipmentMovementController {
//    private final ShipmentMovementService shipmentMovementService;
//
//    @PostMapping("/addShipmentMovement")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ShipmentMovementResponse addShipmentMovement(@RequestBody @Valid ShipmentMovementCreateRequest request) {
//        return shipmentMovementService.addShipmentMovement(request);
//    }
//
//}
