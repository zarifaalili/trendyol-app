package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    @Size(min = 8)
    private String confirmedPassword;

    @NotBlank
    @Pattern(regexp = "^\\+994[0-9]{9}$", message = "phone number must be like: +994XXXXXXXXX ")
    private String phoneNumber;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

}
