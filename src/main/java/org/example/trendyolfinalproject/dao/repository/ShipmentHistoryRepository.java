package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.ShipmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentHistoryRepository extends JpaRepository<ShipmentHistory, Long> {
    List<ShipmentHistory> findByShipmentId_Id(Long id);
}
