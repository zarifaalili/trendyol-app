package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImageCreateRequest {

    @NotNull(message = "Review ID cannot be null")
    private Long reviewId;

    @NotBlank(message = "Image URL cannot be blank")
    private String imageUrl;


}
