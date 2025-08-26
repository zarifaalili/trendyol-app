package org.example.trendyolfinalproject.response;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class ShipmentMovementResponse {
    private Long id;
    private Long shipmentId;
    private String actionNote;
    private String location;
    private String updatedBy;
//    private LocalDateTime timestamp;
}

