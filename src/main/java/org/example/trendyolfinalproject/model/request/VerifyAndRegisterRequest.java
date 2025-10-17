package org.example.trendyolfinalproject.model.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyAndRegisterRequest {
    @Valid
    private VerifyRequest verifyRequest;

    @Valid
    private UserRegisterRequest userRegisterRequest;

}