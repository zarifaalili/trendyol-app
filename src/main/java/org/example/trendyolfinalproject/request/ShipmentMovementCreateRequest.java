package org.example.trendyolfinalproject.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class ShipmentMovementCreateRequest {
    @NotNull(message = "Shipment ID cannot be null")
    private Long shipmentId;
    @NotNull(message = "Action note cannot be null")
    private String actionNote;
    @NotNull(message = "Location cannot be null")
    private String location;
    @NotNull(message = "Updated by cannot be null")
    private String updatedBy;
//    private LocalDateTime timestamp;
}

