package org.example.trendyolfinalproject.scheduler;

// org.example.trendyolfinalproject.service.ShipmentUpdaterService.java

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Order;
import org.example.trendyolfinalproject.dao.entity.Shipment;
import org.example.trendyolfinalproject.dao.entity.ShipmentMovement;
import org.example.trendyolfinalproject.dao.repository.OrderRepository;
import org.example.trendyolfinalproject.dao.repository.ShipmentMovementRepository;
import org.example.trendyolfinalproject.dao.repository.ShipmentRepository;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.service.NotificationService;
import org.example.trendyolfinalproject.service.ShipmentHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentUpdaterService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentMovementRepository shipmentMovementRepository;
    private final ShipmentHistoryService shipmentHistoryService;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    @Transactional
    public void updateShipmentStatusAndNotify(Shipment shipment, Status newStatus, String actionNote) {
        LocalDateTime now = LocalDateTime.now();
        Order order = shipment.getOrder();

        ShipmentMovement movement = new ShipmentMovement();
        movement.setActionNote(actionNote);
        movement.setShipment(shipment);
        movement.setTimestamp(now);
        movement.setLocation("System-Auto");
        movement.setUpdatedBy("System");

        shipment.setStatus(newStatus);
        shipment.setUpdatedAt(now);
        order.setUpdatedAt(now);

        switch (newStatus) {
            case IN_TRANSIT -> order.setStatus(Status.SHIPPED);
            case OUT_FOR_DELIVERY -> order.setStatus(Status.OUT_FOR_DELIVERY);
            case DELIVERED -> order.setStatus(Status.DELIVERED);
        }

        shipmentHistoryService.addupdatedShipmentHistory(
                shipment, "System - Auto", newStatus, now
        );

        shipmentRepository.save(shipment);
        orderRepository.save(order);
        shipmentMovementRepository.save(movement);

        notificationService.sendNotification(
                order.getUser(),
                "Shipment moved to " + actionNote,
                NotificationType.SHIPMENT_MOVEMENT,
                shipment.getId()
        );
        log.info("Shipment {} updated to status {} with actionNote {}", shipment.getId(), newStatus, actionNote);
    }
}