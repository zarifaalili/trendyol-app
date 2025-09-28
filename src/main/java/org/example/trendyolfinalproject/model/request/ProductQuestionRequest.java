package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductQuestionRequest {

    @NotNull(message = "Product ID cannot be null")
    private Long productVariantId;
    @NotNull(message = "Question cannot be null")
    private String question;
}
