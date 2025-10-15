// org.example.trendyolfinalproject.scheduler.ShipmentScheduler.java
package org.example.trendyolfinalproject.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Shipment;
import org.example.trendyolfinalproject.model.enums.Status;
import org.example.trendyolfinalproject.dao.repository.ShipmentRepository; // Bu importu əlavə etdim
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // Bunu yalnız findAll() üçün saxlaya bilərik, amma tələb deyil

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShipmentScheduler {

    private final ShipmentRepository shipmentRepository;
    private final org.example.trendyolfinalproject.scheduler.ShipmentUpdaterService shipmentUpdaterService;

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional(readOnly = false)
    public void autoUpdateShipmentStatus() {
        log.info("ShipmentScheduler: autoUpdateShipmentStatus started at {}", LocalDateTime.now());

//        List<Shipment> shipments = shipmentRepository.findAll();
        List<Shipment> shipments = shipmentRepository.findAllByOrder_StatusNot(Status.CANCELLED);

        for (Shipment shipment : shipments) {
            Status current = shipment.getStatus();
            LocalDateTime updatedAt = shipment.getUpdatedAt();

            if (updatedAt == null) {
                log.warn("Shipment with ID {} has null updatedAt timestamp. Skipping.", shipment.getId());
                continue;
            }
            if (current == Status.PENDING && updatedAt.isBefore(LocalDateTime.now().minusMinutes(1))) {
                shipmentUpdaterService.updateShipmentStatusAndNotify(shipment, Status.SHIPPED, "shipped");
            }else if (current == Status.SHIPPED && updatedAt.isBefore(LocalDateTime.now().minusMinutes(1))) {
                shipmentUpdaterService.updateShipmentStatusAndNotify(shipment, Status.IN_TRANSIT, "courier center");
            } else if (current == Status.IN_TRANSIT && updatedAt.isBefore(LocalDateTime.now().minusMinutes(1))) {
                shipmentUpdaterService.updateShipmentStatusAndNotify(shipment, Status.IN_CUSTOMS, "customs");
            } else if (current == Status.IN_CUSTOMS && updatedAt.isBefore(LocalDateTime.now().minusMinutes(1))) {
                shipmentUpdaterService.updateShipmentStatusAndNotify(shipment, Status.AT_DELIVERY_HUB, "at delivery hub");
            } else if (current == Status.AT_DELIVERY_HUB && updatedAt.isBefore(LocalDateTime.now().minusMinutes(1))) {
                shipmentUpdaterService.updateShipmentStatusAndNotify(shipment, Status.OUT_FOR_DELIVERY, "out for delivery");
            } else if (current == Status.OUT_FOR_DELIVERY && updatedAt.isBefore(LocalDateTime.now().minusMinutes(1))) {
                shipmentUpdaterService.updateShipmentStatusAndNotify(shipment, Status.DELIVERED, "delivered");
                shipment.setActualDeliveryDate(LocalDateTime.now());
                shipmentRepository.save(shipment);
            }



        }
        log.info("ShipmentScheduler: autoUpdateShipmentStatus finished.");
    }
}