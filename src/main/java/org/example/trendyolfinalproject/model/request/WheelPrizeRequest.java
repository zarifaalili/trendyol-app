package org.example.trendyolfinalproject.model.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WheelPrizeRequest {
    @NotNull(message = "Name cannot be null")
    private String name;
    @NotNull(message = "Amount cannot be null")
    private double amount;
    private double minOrder;
}