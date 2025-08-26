package org.example.trendyolfinalproject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.Status;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentHistoryResponse {

    private Long id;
    private Long shipmentId;
    private Status status;
    private String location;
    private LocalDateTime timestamp;
}
