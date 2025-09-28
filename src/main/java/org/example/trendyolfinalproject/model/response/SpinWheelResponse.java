package org.example.trendyolfinalproject.model.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class SpinWheelResponse {

    private Long wheelId;
    private String wheelName;

    private Long prizeId;
    private String prizeName;
    private double amount;
    private double minOrder;

    private LocalDateTime expiresAt;
}