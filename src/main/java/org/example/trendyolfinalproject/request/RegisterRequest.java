package org.example.trendyolfinalproject.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String surname;
}
