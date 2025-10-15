package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyRequest {
    @NotNull(message = "Code cannot be null")
    private final String code;
    @NotNull(message = "Email cannot be null")
    private final String email;
}
