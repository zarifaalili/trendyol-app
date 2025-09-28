package org.example.trendyolfinalproject.model.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.dao.entity.Brand;
import org.example.trendyolfinalproject.dao.entity.Category;
import org.example.trendyolfinalproject.dao.entity.ProductImage;
import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotBlank(message = "Product description cannot be blank")
    private String description;

    @NotNull(message = "Product price cannot be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Category ID cannot be null")
    private Long categoryId;

    @NotNull(message = "Brand ID cannot be null")
    private Long brandId;

//    @NotNull(message = "Seller ID cannot be null")
//    private Long sellerId;

//    private BigDecimal previousPrice;


//    @NotNull(message = "Stock quantity cannot be null")
//    @Min(value = 0, message = "Stock quantity cannot be negative")
//    private Integer stockQuantity;
//
//    private List<String> imageUrls;

//    @NotBlank(message = "SKU cannot be blank")
//    private String sku;

    private BigDecimal weight;
    private BigDecimal dimensions;

}
