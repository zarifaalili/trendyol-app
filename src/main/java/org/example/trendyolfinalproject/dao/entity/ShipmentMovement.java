package org.example.trendyolfinalproject.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
    @Table(name = "shipment_movements")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class ShipmentMovement {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "shipment_id", nullable = false)
        private Shipment shipment;

        private String actionNote;

        private String location;

        private String updatedBy;

        private LocalDateTime timestamp;
    }

