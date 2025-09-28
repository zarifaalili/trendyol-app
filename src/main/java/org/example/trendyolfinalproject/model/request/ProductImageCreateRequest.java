package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProductImageCreateRequest {
    @NotNull(message = "Product ID cannot be null")
    private String imageUrl;
    @NotNull(message = "Is main image cannot be null")
    private Boolean isMainImage;
    @NotNull(message = "Display order cannot be null")
    private Integer displayOrder;
    @NotNull(message = "Alt text cannot be null")
    private String altText;
}
