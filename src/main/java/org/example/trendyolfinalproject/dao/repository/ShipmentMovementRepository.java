package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.ShipmentMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentMovementRepository extends JpaRepository<ShipmentMovement, Long> {
   Optional< ShipmentMovement> findByShipmentIdAndActionNote(Long shipmentId, String actionNote);
}
