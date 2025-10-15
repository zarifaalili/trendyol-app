package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteBasketElementRequest {

    @NotNull(message = "Basket Element ID cannot be null")
    private Long basketElementId;

}
