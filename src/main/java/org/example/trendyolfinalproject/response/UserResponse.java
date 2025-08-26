package org.example.trendyolfinalproject.response;

import lombok.Data;
import org.example.trendyolfinalproject.model.Role;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private Role role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
