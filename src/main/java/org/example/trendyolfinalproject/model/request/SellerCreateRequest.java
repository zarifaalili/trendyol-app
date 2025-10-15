package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.trendyolfinalproject.model.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SellerCreateRequest {


//    @NotNull(message = "User ID cannot be null")
//    private Long userId;

    @NotBlank(message = "Company name cannot be blank")
    @Size(max = 100, message = "Company name cannot exceed 100 characters")
    private String companyName;

    @NotNull(message = "Tax ID cannot be null")
    private Integer taxId;

//    @NotBlank(message = "Contact email cannot be blank")
//    @Email(message = "Invalid email format")
//    @Size(max = 100, message = "Contact email cannot exceed 100 characters")
//    private String contactEmail;

//    @NotNull(message = "Status cannot be null")
//    private Status status;



}
