package org.example.trendyolfinalproject.model.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WheelPrizeRequest {
    private String name;
    private double amount;
    private double minOrder;
}