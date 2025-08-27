package org.example.trendyolfinalproject.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateRequest {

    @NotBlank(message = "Category name cannot be blank")
    private String name;

    @NotNull(message = "Category description cannot be null")
    private String description;

    @NotNull(message = "Parent category ID cannot be null")
    private Long parentCategoryId;
}
