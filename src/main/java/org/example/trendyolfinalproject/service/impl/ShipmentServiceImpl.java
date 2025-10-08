package org.example.trendyolfinalproject.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Order;
import org.example.trendyolfinalproject.dao.entity.Shipment;
import org.example.trendyolfinalproject.dao.repository.ShipmentRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.service.ShipmentHistoryService;
import org.example.trendyolfinalproject.service.ShipmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentHistoryService shipmentHistoryService;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void createShipment(Order order) {
        log.info("Actionlog.createShipment.start : orderId={}", order.getId());
        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setTrackingNumber(Long.valueOf(order.getTrackingNumber()));
        shipment.setCarrierName("Trendyol Cargo");
        shipment.setShippingCost(order.getTotalAmount());
        shipment.setEstimatedDeliveryDate(order.getCreatedAt().plusDays(20));
        shipment.setStatus(Status.PENDING);
        shipment.setCreatedAt(order.getCreatedAt());
        shipment.setUpdatedAt(order.getUpdatedAt());
        shipment.setActualDeliveryDate(LocalDateTime.now().plusDays(20));
        shipmentRepository.save(shipment);
        log.info("Actionlog.createShipment.end : orderId={}", order.getId());

        shipmentHistoryService.addShipmentHistory(shipment);
    }



}
