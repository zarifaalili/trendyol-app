package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Shipment;
import org.example.trendyolfinalproject.dao.repository.OrderRepository;
import org.example.trendyolfinalproject.dao.repository.ShipmentMovementRepository;
import org.example.trendyolfinalproject.dao.repository.ShipmentRepository;
import org.example.trendyolfinalproject.exception.customExceptions.AlreadyException;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.ShipmentMovementMapper;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.model.request.ShipmentMovementCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ShipmentMovementResponse;
import org.example.trendyolfinalproject.service.NotificationService;
import org.example.trendyolfinalproject.service.ShipmentHistoryService;
import org.example.trendyolfinalproject.service.ShipmentMovementService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentMovementServiceImpl implements ShipmentMovementService {
    private final ShipmentMovementRepository shipmentMovementRepository;
    private final ShipmentRepository shipmentRepository;
    private final ShipmentMovementMapper shipmentMovementMapper;
    private final ShipmentHistoryService shipmentHistoryService;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    @Override
    public ApiResponse<ShipmentMovementResponse> addShipmentMovement(ShipmentMovementCreateRequest request) {
        log.info("Actionlog.addShipmentMovement.start : ");
        Shipment shipment = shipmentRepository.findById(request.getShipmentId()).orElseThrow(
                () -> new NotFoundException("Shipment not found")
        );


        var exist = shipmentMovementRepository.findByShipmentIdAndActionNote(request.getShipmentId(), request.getActionNote()).orElse(null);
        if (exist != null) {
            throw new AlreadyException("Shipment movement already exists");
        }
        var entity = shipmentMovementMapper.toEntity(request);
        entity.setTimestamp(LocalDateTime.now());
        entity.setShipment(shipment);
        var order = shipment.getOrder();

        var actionNote = entity.getActionNote();
        switch (actionNote) {
            case "warehouse":
                shipment.setStatus(Status.SHIPPED);
                shipmentHistoryService.addupdatedShipmentHistory(entity.getShipment(), entity.getLocation(), Status.SHIPPED, entity.getTimestamp());
                order.setStatus(Status.PROCESSING);
                break;
            case "courier center":
                shipment.setStatus(Status.IN_TRANSIT);
                shipmentHistoryService.addupdatedShipmentHistory(entity.getShipment(), entity.getLocation(), Status.IN_TRANSIT, entity.getTimestamp());
                order.setStatus(Status.SHIPPED);
                break;

//            case "in transit":
//                shipmentHistoryService.addupdatedShipmentHistory(entity.getShipment(), entity.getLocation(), Status.IN_TRANSIT, entity.getTimestamp());
//                break;
            case "customs":
                shipment.setStatus(Status.IN_CUSTOMS);
                shipmentHistoryService.addupdatedShipmentHistory(entity.getShipment(), entity.getLocation(), Status.IN_CUSTOMS, entity.getTimestamp());
                break;

            case "at delivery hub":
                shipment.setStatus(Status.AT_DELIVERY_HUB);
                shipmentHistoryService.addupdatedShipmentHistory(entity.getShipment(), entity.getLocation(), Status.AT_DELIVERY_HUB, entity.getTimestamp());
                break;
            case "out for delivery":
                shipment.setStatus(Status.OUT_FOR_DELIVERY);
                shipmentHistoryService.addupdatedShipmentHistory(entity.getShipment(), entity.getLocation(), Status.OUT_FOR_DELIVERY, entity.getTimestamp());
                order.setStatus(Status.OUT_FOR_DELIVERY);
                break;
            case "delivered":
                shipment.setStatus(Status.DELIVERED);
                shipmentHistoryService.addupdatedShipmentHistory(entity.getShipment(), entity.getLocation(), Status.DELIVERED, entity.getTimestamp());
                order.setStatus(Status.DELIVERED);
                break;
        }
//        var userId = getCurrentUserId();
        var user = shipment.getOrder().getUser();

        orderRepository.save(order);
        shipmentRepository.save(shipment);
        shipmentMovementRepository.save(entity);

        var response = shipmentMovementMapper.toResponse(entity);
        notificationService.sendNotification(user, "Shipment moved to " + actionNote, NotificationType.SHIPMENT_MOVEMENT, entity.getShipment().getId());
        log.info("Actionlog.addShipmentMovement.end : ");
        return ApiResponse.success(response);

    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }
}
