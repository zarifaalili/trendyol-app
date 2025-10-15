package org.example.trendyolfinalproject.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

public class UserRequest {

    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotBlank(message = "Surname cannot be blank")
    private String surname;
    @NotBlank(message = "phone number cannot be blank")
    private String phoneNumber;
    @NotBlank(message = "Date of birth cannot be blank")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private String dateOfBirth;

}
