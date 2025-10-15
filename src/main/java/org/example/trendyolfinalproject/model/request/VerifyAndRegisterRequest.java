package org.example.trendyolfinalproject.model.request;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class VerifyAndRegisterRequest {
    @Valid
    private VerifyRequest verifyRequest;

    @Valid
    private UserRegisterRequest userRegisterRequest;
}