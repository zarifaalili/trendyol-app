package org.example.trendyolfinalproject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdressResponse {
    private Long id;
    private Long userId;
    private String city;
    private String state;
    private String street;
    private String zipCode;
    private String country;
    private Boolean isDefault;
}
