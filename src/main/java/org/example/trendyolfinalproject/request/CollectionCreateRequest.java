package org.example.trendyolfinalproject.request;

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

//    @NotNull(message = "User id cannot be null")
//    private Long userId;

    @NotBlank(message = "Collection name cannot be blank")
    @Size(max = 100, message = "Collection name cannot exceed 100 characters")
    private String name;


}
