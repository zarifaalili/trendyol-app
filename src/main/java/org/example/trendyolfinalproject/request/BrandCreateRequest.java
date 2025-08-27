package org.example.trendyolfinalproject.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandCreateRequest {

    @NotBlank(message = "Brand name cannot be blank")
    private String name;

    @NotNull(message = "Brand description cannot be null")
    private String description;
}
