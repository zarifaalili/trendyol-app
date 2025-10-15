package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionCreateRequest {

    @NotBlank(message = "Collection name cannot be blank")
    @Size(max = 100, message = "Collection name cannot exceed 100 characters")
    private String name;


}
