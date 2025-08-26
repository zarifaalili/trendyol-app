package org.example.trendyolfinalproject.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerUpdateRequest {

    @NotBlank(message = "Contact email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Contact email cannot exceed 100 characters")
    private String contactEmail;


    @NotNull(message = "Status cannot be null")
    private Status status;


}
