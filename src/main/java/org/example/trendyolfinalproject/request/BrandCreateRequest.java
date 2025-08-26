package org.example.trendyolfinalproject.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandCreateRequest {

    @NotBlank(message = "Brand name cannot be blank")
    private String name;

    private String description;
}
