package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.enums.Status;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentHistoryCreateRequest {

    @NotNull(message = "Shipment ID cannot be null")
    private Long shipmentId;

    @NotNull(message = "Status cannot be null")
    private Status status;

    @NotBlank(message = "Location cannot be blank")
    private String location;

    @NotNull(message = "Timestamp cannot be null")
    private LocalDateTime timestamp;

}
