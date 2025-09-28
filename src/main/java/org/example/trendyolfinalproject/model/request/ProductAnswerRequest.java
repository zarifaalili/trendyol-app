package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductAnswerRequest {
    @NotNull(message = "Product question ID cannot be null")
    private Long productQuestionId;
    @NotNull(message = "Answer cannot be null")
    private  String answer;
}
