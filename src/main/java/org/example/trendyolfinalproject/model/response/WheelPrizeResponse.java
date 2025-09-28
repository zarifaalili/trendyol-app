package org.example.trendyolfinalproject.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WheelPrizeResponse {
    private Long id;
    private String name;
    private double amount;
    private double minOrder;
}