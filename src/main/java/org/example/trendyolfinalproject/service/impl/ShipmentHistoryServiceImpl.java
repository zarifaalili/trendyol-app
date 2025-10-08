package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Shipment;
import org.example.trendyolfinalproject.dao.entity.ShipmentHistory;
import org.example.trendyolfinalproject.dao.repository.ShipmentHistoryRepository;
import org.example.trendyolfinalproject.dao.repository.ShipmentRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.ShipmentHistoryMapper;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ShipmentHistoryResponse;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.ShipmentHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentHistoryServiceImpl implements ShipmentHistoryService {

    private final ShipmentHistoryRepository shipmentHistoryRepository;
    private final ShipmentHistoryMapper shipmentHistoryMapper;
    private final AuditLogService auditLogService;
    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;


    @Override
    public void addShipmentHistory(Shipment shipment) {
        ShipmentHistory shipmentHistory = new ShipmentHistory();
        shipmentHistory.setShipment(shipment);
        shipmentHistory.setStatus(Status.PENDING);
        shipmentHistory.setLocation("WAREHOUSE");
        shipmentHistory.setTimestamp(shipment.getCreatedAt());
        shipmentHistoryRepository.save(shipmentHistory);

    }

    @Override
    public void addupdatedShipmentHistory(Shipment shipment, String location, Status status, LocalDateTime timestamp) {
        ShipmentHistory shipmentHistory = new ShipmentHistory();
        shipmentHistory.setShipment(shipment);
        shipmentHistory.setStatus(status);
        shipmentHistory.setLocation(location);
        shipmentHistory.setTimestamp(timestamp);
        shipmentHistoryRepository.save(shipmentHistory);
    }

    @Override
    public ApiResponse<List<ShipmentHistoryResponse>> getShipmentHistory(Long shipmentId) {
        log.info("Actionlog.getShipmentHistory.start : shipmentId={}", shipmentId);
        var userId = getCurrentUserId();
        var shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new NotFoundException("Shipment not found with id: " + shipmentId));

        var user = shipment.getOrder().getUser();
        if (!user.getId().equals(userId)) {
            throw new RuntimeException("You can not get this shipment history");
        }


        var shipmentHistories = shipmentHistoryRepository.findByShipmentId_Id(shipmentId);
        if (shipmentHistories.isEmpty()) {
            throw new NotFoundException("Shipment not found");
        }

        auditLogService.createAuditLog(user, "Get Shipment History", "Get Shipment History");
        log.info("Actionlog.getShipmentHistory.end : shipmentId={}", shipmentId);
        var response = shipmentHistoryMapper.toResponseList(shipmentHistories);
        return ApiResponse.<List<ShipmentHistoryResponse>>builder()
                .data(response)
                .message("Shipment history found")
                .status(200)
                .build();
    }

    @Override
    public ApiResponse<LocalDateTime> getEstimatedDeliveryDate(Long shipmentId) {
        log.info("Actionlog.getActualDeliveryDate.start : shipmentId={}", shipmentId);
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var shipment = shipmentRepository.findById(shipmentId).orElseThrow(() -> new NotFoundException("Shipment not found with id: " + shipmentId));
        if (!user.getId().equals(shipment.getOrder().getUser().getId())) {
            throw new RuntimeException("You can not get this shipment history");
        }
        var estimatedDeliveryDate = shipment.getEstimatedDeliveryDate();
        log.info("Actionlog.getActualDeliveryDate.end : shipmentId={}", shipmentId);
        var response = estimatedDeliveryDate;
        return ApiResponse.<LocalDateTime>builder()
                .data(response)
                .message("Estimated delivery date found")
                .status(200)
                .build();
    }


    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }

}
